package com.yarr.userservices.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yarr.userservices.dto.AddProductId;
import com.yarr.userservices.dto.OrderResponse;
import com.yarr.userservices.dto.StudentOrderDetailsDTO;
import com.yarr.userservices.dto.UpdateStatusInput;
import com.yarr.userservices.entity.Orders;
import com.yarr.userservices.entity.QueryLogs;
import com.yarr.userservices.services.OrdersService;

@RestController
@RequestMapping("/orders")
public class OrdersController {
  @Autowired
  private OrdersService ordersService;

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @GetMapping("/all")
  public ResponseEntity<OrderResponse> getAllOrders() {
    try {
      List<Orders> orders = ordersService.getAllOrders();
      return ResponseEntity.ok(new OrderResponse(null, orders, "success", "All orders fetched successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE')")
  @GetMapping("/all/{id}")
  public ResponseEntity<OrderResponse> getMyOrders(@PathVariable String id) {
    try {
      List<Orders> myOrders = ordersService.getOrdersByUserId(id);
      return ResponseEntity.ok(new OrderResponse(null, myOrders, "success", "All orders fetched successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
    try {
      Orders order = ordersService.getOrderById(UUID.fromString(id));
      if (order != null) {
        return ResponseEntity.ok(new OrderResponse(order, null, "success", "Order fetched successfully"));
      } else {
        return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", "Order not found"));
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @PostMapping("/create")
  public ResponseEntity<OrderResponse> addNewOrder(@RequestBody Orders order) {
    try {
      Orders newOrder = ordersService.addNewOrder(order);
      return ResponseEntity.ok(new OrderResponse(newOrder, null, "success", "Order added successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @PatchMapping("/choose-template/{id}")
  public ResponseEntity<OrderResponse> addNewOrder(@PathVariable String id, @RequestBody AddProductId templateId) {
    try {
      Orders newOrder = ordersService.addTemplateId(UUID.fromString(id), templateId.getTemplateId());
      return ResponseEntity.ok(new OrderResponse(newOrder, null, "success", "Order added successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE') or hasRole('SUPERADMIN')")
  @PostMapping("/open/{id}/{session}/{productReference}")
  public ResponseEntity<OrderResponse> openOrderForProduction(@PathVariable String id, @PathVariable String session,
      @PathVariable String productReference, @RequestBody UpdateStatusInput updateStatusInput) {
    try {
      Orders newOrder = ordersService.openOrderForProduction(UUID.fromString(id), updateStatusInput.getOrderLog(),
          session, productReference, updateStatusInput.getStatus_view());
      return ResponseEntity
          .ok(new OrderResponse(newOrder, null, "success", "Order opened for production successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE') or hasRole('SUPERADMIN') or hasRole('ADMIN')")
  @PatchMapping("/updateStatus/{id}")
  public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable String id,
      @RequestBody UpdateStatusInput updateStatusInput) {
    try {
      Orders updatedOrder = ordersService.updateOrderStatus(UUID.fromString(id), updateStatusInput.getOrderLog(),
          updateStatusInput.getStatus_view());
      return ResponseEntity.ok(new OrderResponse(updatedOrder, null, "success", "Order status updated successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE')")
  @DeleteMapping("/remove/{id}")
  public ResponseEntity<OrderResponse> removeOrderFromCart(@PathVariable String id) {
    try {
      Orders removedOrder = ordersService.removeOrderFromCart(UUID.fromString(id));
      return ResponseEntity
          .ok(new OrderResponse(removedOrder, null, "success", "Order removed from cart successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PostMapping("/chat/{id}")
  public ResponseEntity<OrderResponse> chatForAOrder(@PathVariable String id, @RequestBody QueryLogs queryLog) {
    try {
      Orders updatedOrder = ordersService.createConversation(UUID.fromString(id), queryLog);
      return ResponseEntity.ok(new OrderResponse(updatedOrder, null, "success", "Conversation created successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PatchMapping("/removeChat/{id}")
  public ResponseEntity<OrderResponse> removeChatForAOrder(@PathVariable String id, @RequestBody QueryLogs queryLog) {
    try {
      Orders updatedOrder = ordersService.deleteConversation(UUID.fromString(id), queryLog);
      return ResponseEntity.ok(new OrderResponse(updatedOrder, null, "success", "Conversation removed successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PatchMapping("/updateChat/{id}")
  public ResponseEntity<OrderResponse> updateChatForAOrder(@PathVariable String id, @RequestBody QueryLogs queryLog,
      @RequestParam String type) {
    try {
      Orders updatedOrder = ordersService.updateParticularConversation(UUID.fromString(id), queryLog, type);
      return ResponseEntity.ok(new OrderResponse(updatedOrder, null, "success", "Conversation updated successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PatchMapping("/clearAllChats/{id}")
  public ResponseEntity<OrderResponse> clearAllChatsForAOrder(@PathVariable String id) {
    try {
      Orders updatedOrder = ordersService.clearAllConversations(UUID.fromString(id));
      return ResponseEntity
          .ok(new OrderResponse(updatedOrder, null, "success", "All conversations cleared successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<OrderResponse> deleteOrder(@PathVariable String id) {
    try {
      Boolean deletedOrder = ordersService.deleteOrder(UUID.fromString(id));
      if (deletedOrder) {
        return ResponseEntity.ok(new OrderResponse(null, null, "success", "Order deleted successfully"));
      } else {
        return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", "Order not found"));
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PostMapping("/cancelOrder/{id}")
  public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String id,
      @RequestBody UpdateStatusInput updateStatusInput) {
    try {
      Orders updatedOrder = ordersService.cancelOrder(UUID.fromString(id), updateStatusInput.getOrderLog(),
          updateStatusInput.getStatus_view());
      return ResponseEntity.ok(new OrderResponse(updatedOrder, null, "success", "Order cancelled successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
  @PatchMapping("/addServiceChargeAndTax/{id}")
  public ResponseEntity<OrderResponse> addServiceChargeAndTax(@PathVariable String id,
      @RequestParam Float serviceCharge, @RequestParam Float tax) {
    try {
      Orders updatedOrder = ordersService.addServiceChargeAndTaxAmount(UUID.fromString(id), serviceCharge, tax);
      return ResponseEntity
          .ok(new OrderResponse(updatedOrder, null, "success", "Service charge and tax added successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @PreAuthorize("hasRole('INSTITUTE') or hasRole('SUPERADMIN') or hasRole('ADMIN')")
  @PatchMapping("/updatePaymentMode/{id}")
  public ResponseEntity<OrderResponse> updatePaymentMode(@PathVariable String id, @RequestParam String paymentMode) {
    try {
      Orders updatedOrder = ordersService.updatePaymentMode(UUID.fromString(id), paymentMode);
      return ResponseEntity.ok(new OrderResponse(updatedOrder, null, "success", "Payment mode updated successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage()));
    }
  }

  @GetMapping("/student-order-details")
  public ResponseEntity<OrderResponse> getStudentOrderDetails(
      @RequestParam String studentId,
      @RequestParam String instituteId,
      @RequestParam(required = false, defaultValue = "PENDING") String studentStatus) {

    try {
      List<StudentOrderDetailsDTO> details = ordersService.getStudentOrderDetails(studentId, instituteId,
          studentStatus);
      return ResponseEntity.ok(new OrderResponse(null, null, "success",
          details.isEmpty() ? "No order details found" : "Order details fetched successfully", details));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new OrderResponse(null, null, "error", e.getMessage(), null));
    }
  }
}
