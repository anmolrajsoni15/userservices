package com.yarr.userservices.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yarr.userservices.entity.IdData;
import com.yarr.userservices.entity.OrderLogs;
import com.yarr.userservices.entity.Orders;
import com.yarr.userservices.entity.Product;
import com.yarr.userservices.entity.QueryLogs;
import com.yarr.userservices.entity.StudentDataEntity;
import com.yarr.userservices.repository.IdDataRepository;
import com.yarr.userservices.repository.OrderRepository;
import com.yarr.userservices.repository.ProductRepository;
import com.yarr.userservices.repository.StudentDataRepository;
import com.yarr.userservices.client.AdminServiceClient;
import com.yarr.userservices.client.NotificationServiceClient;
import com.yarr.userservices.dto.StudentOrderDetailsDTO;
import com.yarr.userservices.dto.Template;
import com.yarr.userservices.dto.NotificationDTO;

@Service
public class OrdersService {

  private final OrderRepository orderRepository;
  private final IdDataRepository idDataRepository;
  private final StudentDataRepository studentDataRepository;
  private final ProductRepository productRepository;
  private final AdminServiceClient adminServiceClient;
  private final NotificationServiceClient notificationServiceClient;

  public OrdersService(OrderRepository orderRepository, IdDataRepository idDataRepository,
      StudentDataRepository studentDataRepository, ProductRepository productRepository,
      AdminServiceClient adminServiceClient, NotificationServiceClient notificationServiceClient) {
    this.orderRepository = orderRepository;
    this.idDataRepository = idDataRepository;
    this.studentDataRepository = studentDataRepository;
    this.productRepository = productRepository;
    this.adminServiceClient = adminServiceClient;
    this.notificationServiceClient = notificationServiceClient;
  }

  public Orders getOrderById(UUID id) {
    return orderRepository.findById(id).orElse(null);
  }

  public List<Orders> getOrdersByUserId(String userId) {
    List<Orders> orders = orderRepository.findAllByUserId(userId);
    if (orders == null) {
      return new ArrayList<>();
    }
    orders.sort((a, b) -> b.getOrder_date().compareTo(a.getOrder_date()));
    return orders;
  }

  public List<Orders> getAllOrders() {
    List<Orders> orders = orderRepository.findAll();
    if (orders == null) {
      return new ArrayList<>();
    }

    orders.removeIf(
        order -> order.getStatus().equals("IN-THE-CART"));
    orders.sort((a, b) -> b.getOrder_date().compareTo(a.getOrder_date()));
    return orders;
  }

  public Orders addNewOrder(Orders order) {
    order.setId(UUID.randomUUID());
    return orderRepository.save(order);
  }

  public Orders openOrderForProduction(UUID id, OrderLogs orderLog, String session, String productReference,
      String viewStatus) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    order.setStatus(orderLog.getStatus());
    order.setSession(session);
    order.setProduct_reference(productReference);
    order.setStatus_view(viewStatus);
    List<OrderLogs> orderLogs = order.getOrder_logs();
    if (orderLogs == null) {
      orderLogs = new ArrayList<>();
    }
    orderLogs.add(orderLog);
    order.setOrder_logs(orderLogs);
    order.setLast_updated(orderLog.getDate_time());
    
    Orders savedOrder = orderRepository.save(order);
    
    // Send notification to admins
    try {
      NotificationDTO notification = NotificationDTO.builder()
          .id(UUID.randomUUID())
          .message("New order opened for production for session " + session)
          .type("ORDER_PRODUCTION")
          .recipientRole("ADMIN")
          .senderId(order.getUser_id())
          .senderName(orderLog.getUsermail())
          .referenceId(id.toString())
          .referenceType("ORDER")
          .read(false)
          .createdAt(Instant.now())
          .build();
      
      notificationServiceClient.createNotification(notification);
    } catch (Exception e) {
      // Log error but don't fail the order creation
      System.err.println("Failed to send notification: " + e.getMessage());
    }
    
    return savedOrder;
  }

  public void updateOnlyStatus(UUID id, String status) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return;
    }

    order.setStatus(status);
    order.setLast_updated(Instant.now());
    orderRepository.save(order);
  }

  public Orders updateOrderStatus(UUID id, OrderLogs orderLog, String viewStatus) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    order.setStatus(orderLog.getStatus());
    List<OrderLogs> orderLogs = order.getOrder_logs();
    if (orderLogs == null) {
      orderLogs = new ArrayList<>();
    }
    orderLogs.add(orderLog);
    order.setOrder_logs(orderLogs);
    order.setLast_updated(orderLog.getDate_time());
    order.setStatus_view(viewStatus);
    return orderRepository.save(order);
  }

  public Orders removeOrderFromCart(UUID id) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order != null && order.getStatus().equals("IN-THE-CART")) {
      orderRepository.delete(order);
      return order;
    }
    return null;
  }

  public Orders createConversation(UUID id, QueryLogs queryLog) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    List<QueryLogs> queryLogs = order.getQuery_logs();
    if (queryLogs == null) {
      queryLogs = new ArrayList<>();
    }
    queryLogs.add(queryLog);
    // sort the querylogs in ascending order of date_time
    queryLogs.sort((a, b) -> a.getDate_time().compareTo(b.getDate_time()));
    order.setQuery_logs(queryLogs);
    return orderRepository.save(order);
  }

  public Orders addTemplateId(UUID id, String templateId) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    order.setTemplate_id(templateId);
    return orderRepository.save(order);
  }

  public Orders deleteConversation(UUID id, QueryLogs queryLog) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    List<QueryLogs> queryLogs = order.getQuery_logs();
    if (queryLogs == null) {
      queryLogs = new ArrayList<>();
    }
    queryLogs.remove(queryLog);
    // sort the querylogs in ascending order of date_time
    queryLogs.sort((a, b) -> a.getDate_time().compareTo(b.getDate_time()));
    order.setQuery_logs(queryLogs);
    return orderRepository.save(order);
  }

  public Orders updateParticularConversation(UUID id, QueryLogs queryLog, String type) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    List<QueryLogs> queryLogs = order.getQuery_logs();

    QueryLogs updatedQueryLogs = new QueryLogs();
    updatedQueryLogs.setFrom_email(queryLog.getFrom_email());
    updatedQueryLogs.setFrom_role(queryLog.getFrom_role());
    updatedQueryLogs.setTo_email(queryLog.getTo_email());
    updatedQueryLogs.setTo_role(queryLog.getTo_role());
    updatedQueryLogs.setQuery(queryLog.getQuery());
    updatedQueryLogs.setType(type);
    updatedQueryLogs.setDate_time(queryLog.getDate_time());

    if (queryLogs != null) {
      queryLogs.remove(queryLog);
    } else {
      queryLogs = new ArrayList<>();
    }

    queryLogs.add(updatedQueryLogs);
    // sort the querylogs in ascending order of date_time
    queryLogs.sort((a, b) -> a.getDate_time().compareTo(b.getDate_time()));
    order.setQuery_logs(queryLogs);
    return orderRepository.save(order);
  }

  public Orders clearAllConversations(UUID id) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    order.setQuery_logs(new ArrayList<>());
    return orderRepository.save(order);
  }

  public Boolean deleteOrder(UUID id) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return false;
    }

    String productRef = order.getProduct_reference();
    if (productRef != null) {
      idDataRepository.deleteById(UUID.fromString(productRef));
    }

    orderRepository.delete(order);
    return true;
  }

  public Orders cancelOrder(UUID id, OrderLogs orderLog, String statusView) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    String productRef = order.getProduct_reference();
    if (productRef != null) {
      idDataRepository.deleteById(UUID.fromString(productRef));
    }

    order.setStatus("ORDER-CANCELED");
    order.setProduct_reference(null);
    order.setLast_updated(Instant.now());
    List<OrderLogs> orderLogs = order.getOrder_logs();
    if (orderLogs == null) {
      orderLogs = new ArrayList<>();
    }
    orderLogs.add(orderLog);
    order.setOrder_logs(orderLogs);
    order.setStatus_view(statusView);

    orderRepository.save(order);
    return order;
  }

  public Orders addServiceChargeAndTaxAmount(UUID id, Float serviceCharge, Float tax) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    order.setService_charge(serviceCharge);
    order.setTax(tax);
    return orderRepository.save(order);
  }

  public Orders updatePaymentMode(UUID id, String paymentMode) {
    Orders order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    order.setPayment_mode(paymentMode);
    return orderRepository.save(order);
  }

  public List<StudentOrderDetailsDTO> getStudentOrderDetails(String studentId, String instituteId,
      String excludeStatus) {
    List<StudentOrderDetailsDTO> result = new ArrayList<>();

    // Step 1: Find all IdData entries that contain this studentId in their
    // student_ids list
    List<IdData> idDataList = idDataRepository.findAll().stream()
        .filter(idData -> idData.getStudent_ids() != null &&
            idData.getStudent_ids().contains(studentId) &&
            idData.getInstitute_id().equals(instituteId))
        .collect(Collectors.toList());

    for (IdData idData : idDataList) {
      // Step 2: Get the StudentDataEntity for this student from each IdData
      StudentDataEntity studentDataEntity = studentDataRepository.findByStudentIdAndIdDataId(studentId, idData.getId());

      // Skip if student data not found or status matches the exclude status (e.g.,
      // "PENDING")
      if (studentDataEntity == null || excludeStatus.equals("PENDING") || excludeStatus.equals("REJECTED") || excludeStatus.equals("SUBMITTED")) {
        continue;
      }

      // Step 3: Find orders related to this ID data
      List<Orders> orders = orderRepository.findByProductReferenceAndProductType(
          idData.getId().toString(), "idcard");

      for (Orders order : orders) {
        // Ensure order belongs to this institute
        if (!instituteId.equals(order.getUser_id())) {
          continue;
        }

        StudentOrderDetailsDTO detailDTO = new StudentOrderDetailsDTO();
        detailDTO.setOrderId(order.getId());
        detailDTO.setProduct_type(order.getProduct_type());
        detailDTO.setSession(order.getSession());

        // Extract order logs - but only after "REQUESTED-APPLICATION"
        List<OrderLogs> allOrderLogs = order.getOrder_logs();
        List<StudentOrderDetailsDTO.OrderLogDTO> orderLogDTOs = new ArrayList<>();

        if (allOrderLogs != null && !allOrderLogs.isEmpty()) {
          // Find the index of "REQUESTED-APPLICATION" log
          int requestedAppIndex = -1;
          for (int i = 0; i < allOrderLogs.size(); i++) {
            if ("REQUESTED-APPLICATION".equals(allOrderLogs.get(i).getStatus())) {
              requestedAppIndex = i;
              break;
            }
          }

          // If found, include only logs after that index
          if (requestedAppIndex >= 0) {
            for (int i = requestedAppIndex+1; i < allOrderLogs.size(); i++) {
              OrderLogs log = allOrderLogs.get(i);
              orderLogDTOs.add(new StudentOrderDetailsDTO.OrderLogDTO(log.getStatus(), log.getDate_time()));
            }
          }
        }

        detailDTO.setOrderLogs(orderLogDTOs);

        // Create product details
        StudentOrderDetailsDTO.ProductInfoDTO productInfo = new StudentOrderDetailsDTO.ProductInfoDTO();

        // Get product information if available
        if (order.getProduct_id() != null) {
          Product product = productRepository.findById(UUID.fromString(order.getProduct_id())).orElse(null);
          if (product != null) {
            productInfo.setName(product.getName());
            productInfo.setDescription(product.getDescription());
            productInfo.setPrice(product.getPrice());
            productInfo.setDiscount(product.getDiscount());
          }
        }

        // Get template information if available
        if (order.getTemplate_id() != null) {
          Template template = adminServiceClient.getTemplateById(order.getTemplate_id());
          if (template != null) {
            productInfo.setDimension(template.getDimensions());
            productInfo.setFront_url(template.getFront_url());
            productInfo.setBack_url(template.getBack_url());
          }
        }

        // Add service charge and tax from order
        productInfo.setService_charge(order.getService_charge());
        productInfo.setTax(order.getTax());

        detailDTO.setProduct(productInfo);
        result.add(detailDTO);
      }
    }

    return result;
  }

}
