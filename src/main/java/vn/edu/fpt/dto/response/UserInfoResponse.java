package vn.edu.fpt.dto.response;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String cccd;
    private String gender;
    private String phoneNumber;
    private String avatarUrl;
    private Set<String> roles;
    private Object profile; // Can be DoctorProfile or PatientProfile

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorProfile {
        private String doctorId;
        private String specialization;
        private String degree;
        private String licenseNumber;
        private Boolean isHeadOfDept;
        private RoomInfo room;
        private String bio;
        private DepartmentInfo department;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DepartmentInfo {
            private Integer deptId;
            private String deptName;
            private String deptType;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class RoomInfo {
            private String roomId;
            private String roomName;
            private String roomType;
            private String location;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientProfile {
        private String patientId;
        private String dateOfBirth;
        private String bloodType;
        private String healthInsuranceNumber;
        private String allergies;
        private String address;
    }
}
