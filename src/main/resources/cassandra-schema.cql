CREATE TYPE IF NOT EXISTS idservices.query_logs (
    from_role text,
    from_email text,
    to_role text,
    to_email text,
    query text,
    type text,
    date_time timestamp
);

CREATE TYPE IF NOT EXISTS idservices.order_logs (
    status text,
    date_time timestamp,
    usermail text,
    userrole list<text>
);

CREATE TYPE IF NOT EXISTS idservices.user_form_request (
    label text,
    description text,
    type text,
    side text,
    modification_date timestamp,
);

CREATE TYPE IF NOT EXISTS idservices.form_data (
    id text,
    key text,
    label text,
    type text,
    side text,
    show_label boolean,
    style text,
    value text,
    value_type text
);

// Create a new table for student data instead of UDT
CREATE TABLE IF NOT EXISTS idservices.student_data (
    id uuid PRIMARY KEY,
    student_id text,
    institute_id text,
    form_data list<frozen<form_data>>,
    status text,
    is_continuing boolean,
    session text,
    created_at timestamp,
    updated_at timestamp,
    id_data_id uuid
);

// Add indices for student_data table to improve query performance
CREATE INDEX IF NOT EXISTS ON idservices.student_data (student_id);
CREATE INDEX IF NOT EXISTS ON idservices.student_data (institute_id);
CREATE INDEX IF NOT EXISTS ON idservices.student_data (id_data_id);
CREATE INDEX IF NOT EXISTS ON idservices.student_data (status);

CREATE TABLE IF NOT EXISTS idservices.id_data (
    id uuid PRIMARY KEY,
    institute_id text,
    form_data list<frozen<form_data>>,
    student_ids list<text>,
    user_form_request list<frozen<user_form_request>>,
    form_closing_time timestamp,
    is_form_closed boolean,
    session text,
    created_at timestamp
);

CREATE TABLE IF NOT EXISTS idservices.orders (
    id uuid PRIMARY KEY,
    user_id text,
    product_id text,
    template_id text,
    product_type text,
    product_reference text,
    status_view text,
    quantity int,
    service_charge float,
    tax float,
    payment_mode text,
    total_price float,
    session text,
    status text,
    order_date timestamp,
    query_logs list<frozen<query_logs>>,
    order_logs list<frozen<order_logs>>,
    last_updated timestamp
);

CREATE TABLE IF NOT EXISTS idservices.product (
    id uuid PRIMARY KEY,
    name text,
    description text,
    photo list<text>,
    created_at timestamp,
    updated_at timestamp,
    created_by text,
    price float,
    discount float,
);

