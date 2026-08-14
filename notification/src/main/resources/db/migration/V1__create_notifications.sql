CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(320) NOT NULL,
    title VARCHAR(500) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    status VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);
