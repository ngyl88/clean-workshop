package chrisza.purchasing.domain;

import java.util.List;
import java.util.UUID;

public class PurchaseRequest {
    private UUID id;
    private final List<PurchaseRequestItem> Items;
    private final Employee Owner;
    private final Employee Approver;

    private static final int AMOUNT_REQUIRING_SENIOR_MANAGEMENT_APPROVAL = 100_000;

    public PurchaseRequest(List<PurchaseRequestItem> items, Employee owner, Employee approver) {
        Items = items;
        Owner = owner;
        Approver = approver;
    }

    public void validate() throws PurchaseRequestException {

        checkRequestOwnerAndApprover(Owner, Approver);
        checkApproverByTotalPurchase(Items, Approver);

    }

    private static void checkRequestOwnerAndApprover(Employee owner, Employee approver) throws PurchaseRequestException {
        if(!approver.isManagement()) {
            throw new PurchaseRequestException("Approver need to be Management level.");
        }

        if (!owner.isManagedUnder(approver)) {
            throw new PurchaseRequestException("Invalid approver hierarchy.");
        }
    }

    private static void checkApproverByTotalPurchase(List<PurchaseRequestItem> items, Employee approver) throws PurchaseRequestException {
        int totalPurchase = items.stream()
                .mapToInt(PurchaseRequestItem::getTotalPrice)
                .sum();

        if (totalPurchase >= AMOUNT_REQUIRING_SENIOR_MANAGEMENT_APPROVAL && !approver.isSeniorManagement()) {
            throw new PurchaseRequestException("Require Senior Management approval for purchase amount.");
        }
    }

    /* Getters and Setters */

    public Employee getApprover() {
        return Approver;
    }

    public Employee getOwner() {
        return Owner;
    }

    public List<PurchaseRequestItem> getItems() {
        return Items;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
