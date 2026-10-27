package se.iths.christoffer.orderservicegroup2.model;

/**
 * Möjliga statusar för en order.
 */
public enum OrderStatus {
    /**
     * Ordern är skapad och väntar på betalning.
     */
    PENDING,
    /**
     * Ordern är betald och slutförd.
     */
    COMPLETED,
    /**
     * Ordern kunde inte genomföras.
     */
    FAILED,
    /**
     * Ordern har avbrutits.
     */
    CANCELLED
}
