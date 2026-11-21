package chocan.data;

import chocan.model.*;

import java.math.BigDecimal;

public class SeedData {

    public static void populate(ChocAnDatabase db) {

        // Sample staff accounts
        db.putStaff(new StaffAccount(
                "900000001", "System Operator", Role.OPERATOR));

        db.putStaff(new StaffAccount(
                "900000002", "System Manager", Role.MANAGER));

        // Sample members
        db.putMember(new Member(
                "100000001",
                new PersonDetails(
                        "Alice Johnson",
                        "123 Main Street",
                        "Tuscaloosa",
                        "AL",
                        "35401"),
                MemberStatus.VALID));

        db.putMember(new Member(
                "100000002",
                new PersonDetails(
                        "Bob Smith",
                        "456 Oak Avenue",
                        "Tuscaloosa",
                        "AL",
                        "35401"),
                MemberStatus.SUSPENDED));

        // Sample providers
        db.putProvider(new Provider(
                "200000001",
                new PersonDetails(
                        "Dr. Emily Brown",
                        "789 Medical Drive",
                        "Tuscaloosa",
                        "AL",
                        "35401")));

        db.putProvider(new Provider(
                "200000002",
                new PersonDetails(
                        "Dr. James Wilson",
                        "321 Health Road",
                        "Birmingham",
                        "AL",
                        "35203")));

        // Sample services
        db.putService(new ServiceType(
                "100001",
                "Nutrition Counseling",
                new BigDecimal("75.00")));

        db.putService(new ServiceType(
                "100002",
                "Diet Consultation",
                new BigDecimal("100.00")));

        db.putService(new ServiceType(
                "100003",
                "Group Therapy",
                new BigDecimal("50.00")));
    }
}