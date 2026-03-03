package it.schwarz.jobs.review.coupon.testobjects;

public class TestObjects {

    private final static TestCoupons testCoupons = new TestCoupons();
    private final static TestApplications testApplications = new TestApplications();
    private final static TestRequests testRequests = new TestRequests();

    private TestObjects() {
    }

    public static TestCoupons coupons() {
        return testCoupons;
    }

    public static TestApplications applications() {
        return testApplications;
    }

    public static TestRequests requests() {
        return testRequests;
    }

}
