package com.aikaload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.aikaload.entity.*;
import com.aikaload.repo.*;
import com.aikaload.utils.VariableUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;





@Component
@AllArgsConstructor
public class DatabaseLoader implements CommandLineRunner {
    private final UserRoleRepo userRoleRepo;
    private final UserAccountRepo userAccountRepo;
    private final JobTypeRepo jobTypeRepo;
    private final JobCategoryRepo jobCategoryRepo;
    private final TruckTypeRepo truckTypeRepo;
    private final VehicleInfoRepo vehicleInfoRepo;
    private final DriverInfoRepo driverInfoRepo;
    private final LoadCategoryRepo loadCategoryRepo;
    private final PackageSpaceRepo packageSpaceRepo;
    private final OauthClientDetailsRepo oauthClientDetailsRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoadLevelRepo loadLevelRepo;
    private final TruckModelRepo truckModelRepo;
    private final FeeConfigurationRepo feeConfigurationRepo;

    private void populateDb() {
        List<LoadCategory> loadCategoryList = new ArrayList<>();
        if(loadCategoryRepo.findAll().size() ==  2){
            loadCategoryRepo.deleteAll();
            loadCategoryList.add(new LoadCategory("VEHICLES & BOATS (Cars, Boats, Motorcycles, Parts)"));
            loadCategoryList.add(new LoadCategory("HOUSE ITEMS (Furniture, Appliances)"));
            loadCategoryList.add(new LoadCategory("MOVES (Apartment, Home, Office)"));
            loadCategoryList.add(new LoadCategory("HEAVY EQUIPMENT (Farm, Construction)"));
            loadCategoryList.add(new LoadCategory("FREIGHT ( Full Truck Load, Less-than Truck Load)"));
            loadCategoryList.add(new LoadCategory("ANIMALS (Livestock, Horses, Dog, cats)"));
            loadCategoryList.add(new LoadCategory("GRAINS & FARM PRODUCE (Perishable, Non-perishable)"));
            loadCategoryList.add(new LoadCategory("CONTAINER"));
            loadCategoryList.add(new LoadCategory("OTHERS"));
            loadCategoryRepo.saveAll(loadCategoryList);
          }


        List<LoadLevel> loadLevels = new ArrayList<>();
        if(loadLevelRepo.findAll().isEmpty()) {
            loadLevels.add(new LoadLevel("Full Load"));
            loadLevels.add(new LoadLevel("Half Load"));
            loadLevelRepo.saveAll(loadLevels);
        }




        List<PackageSpace> packageSpaceList = new ArrayList<>();
        if (packageSpaceRepo.findAll().isEmpty()) {
            PackageSpace p1 = new PackageSpace();
            p1.setName("Upto 7 MT");
            packageSpaceList.add(p1);

            PackageSpace p2 = new PackageSpace();
            p2.setName("Upto 9 MT");
            packageSpaceList.add(p2);

            packageSpaceRepo.saveAll(packageSpaceList);
        }

            List<UserRole> userRoleList = new ArrayList<>();
             if(userRoleRepo.findAll().isEmpty()) {
                userRoleList.add(new UserRole("Load Owner", "Owns load"));
                userRoleList.add(new UserRole("Truck Owner", "Owns truck"));
                userRoleList.add(new UserRole("Driver", "driver"));
                userRoleList.add(new UserRole(VariableUtil.ADMIN, "manages application"));
                userRoleRepo.saveAll(userRoleList);
            }



        List<JobType> jobTypeList = new ArrayList<>();
        if (jobTypeRepo.findAll().isEmpty()) {
            JobType jobType1 = new JobType();
            jobType1.setName("NORMAL");
            jobTypeList.add(jobType1);

            JobType jobType2 = new JobType();
            jobType2.setName("URGENT");
            jobTypeList.add(jobType2);

            JobType jobType3 = new JobType();
            jobType3.setName("SUPER SURGE");
            jobTypeList.add(jobType3);

            jobTypeRepo.saveAll(jobTypeList);
        }

        List<JobCategory> jobCategoryList = new ArrayList<>();
        if (jobCategoryRepo.findAll().isEmpty()) {
            JobCategory jobCategory1 = new JobCategory();
            jobCategory1.setName("OTHER");
            jobCategoryList.add(jobCategory1);
            jobCategoryRepo.saveAll(jobCategoryList);
        }

        List<TruckType> truckTypeList = new ArrayList<>();
        if (truckTypeRepo.findAll().isEmpty()) {
            truckTypeList.add(new TruckType("COVERED 5TONS"));
            truckTypeList.add(new TruckType("OPEN 5TONS"));
            truckTypeList.add(new TruckType("OPEN 10TONS"));
            truckTypeList.add(new TruckType("COVERED 10TONS"));
            truckTypeList.add(new TruckType(" OPEN 14TONS"));
            truckTypeRepo.saveAll(truckTypeList);
        }


        List<TruckModel> truckModelList = new ArrayList<>();
        if (truckModelRepo.findAll().isEmpty()) {
            truckModelList.add(new TruckModel("Truck Model 2005"));
            truckModelList.add(new TruckModel("Truck Model 2008"));
            truckModelRepo.saveAll(truckModelList);
        }


        if (vehicleInfoRepo.findAll().isEmpty()) {
            VehicleInfo vehicleInfo = new VehicleInfo();
            vehicleInfo.setGitInsuranceExpirDate(new Date());
            vehicleInfo.setVehiclePlateNumber("XYW 154 BD");
            vehicleInfo.setVehicleModelName("TOYOTA COROLLA");
            vehicleInfo.setVehicleModelYear("2012");
            vehicleInfo.setIsVerified(1);
            vehicleInfoRepo.save(vehicleInfo);
        }

        if (driverInfoRepo.findAll().isEmpty()) {
            DriverInfo driverInfo = new DriverInfo();
            driverInfo.setAddress("test");
            driverInfo.setDriverName("DRIVER 1");
            driverInfo.setMobileNumber("234505940394");
            driverInfoRepo.save(driverInfo);
        }

        if (feeConfigurationRepo.findAll().isEmpty()) {
            FeeConfiguration feeConfiguration = new FeeConfiguration();
            feeConfiguration.setVerificationCost(new BigDecimal("1000"));
            feeConfigurationRepo.save(feeConfiguration);
        }


        //Create User
      // if(userAccountRepo.findAll().isEmpty()){
            UserAccount userAccount = new UserAccount();
            userAccount.setCompanyName("UI-CONNECTION");
            userAccount.setMobilePhone("08060000000");
            userAccount.setContactPersonName("UI CONTACT PERSON");
            userAccount.setPassword(bCryptPasswordEncoder.encode("UI2324@123"));
            userAccount.setUsername("ABXUIVERT-UI");
            userAccount.setAccountInformationUpdated(false);
            userAccount.setAccountVerified(true);
            userAccount.setDateCreated(new Date());
            userAccount.setReferredBy(VariableUtil.REFERRED_BY_DEFAULT);
            userAccount.setHasActiveBidPackagePlan(true);

            Optional<UserRole> userRole = userRoleRepo.findById(4L);
            userRole.ifPresent(userAccount::setUserRole);
            userAccountRepo.save(userAccount);

           //FOR ADMIN
           UserAccount userAccount2 = new UserAccount();
           userAccount2.setCompanyName("ADMIN API CALLER");
           userAccount2.setMobilePhone("080########");
           userAccount2.setContactPersonName("ADMIN API CALLER");
           userAccount2.setPassword(bCryptPasswordEncoder.encode("7034S7LU5"));
           userAccount2.setUsername("ABse2IVERT-admin");
           userAccount2.setAccountInformationUpdated(false);
           userAccount2.setAccountVerified(true);
           userAccount2.setDateCreated(new Date());

           Optional<UserRole> userRole2 = userRoleRepo.findById(4L);
           userRole2.ifPresent(userAccount::setUserRole);
           userAccountRepo.save(userAccount);

       // }


        //Create oauthClient User
       // if(oauthClientDetailsRepo.findAll().isEmpty()){
            //======For UI
           /* OauthClientDetails oauthClientDetails = new OauthClientDetails();
            oauthClientDetails.setClientId("glee-o-meter");
            oauthClientDetails.setClientSecret(bCryptPasswordEncoder.encode("secret"));
            oauthClientDetails.setAccessTokenValidity(900);
            oauthClientDetails.setAuthorizedGrantTypes("password,authorization_code,refresh_token");
            oauthClientDetails.setAuthorities("ROLE_USER");
            oauthClientDetails.setAutoApprove(true);
            oauthClientDetails.setRefreshTokenValidity(9000);
            oauthClientDetails.setResourceIds("api");
            oauthClientDetails.setScope("read,write");
            oauthClientDetailsRepo.save(oauthClientDetails);*/

            //For Admin
          /*  OauthClientDetails oauthClientDetails = new OauthClientDetails();
            oauthClientDetails.setClientId("aikaloadadmim23");
            oauthClientDetails.setClientSecret(bCryptPasswordEncoder.encode("4246SRGOW5469S5NTK"));
            oauthClientDetails.setAccessTokenValidity(900);
            oauthClientDetails.setAuthorizedGrantTypes("password,authorization_code,refresh_token");
            oauthClientDetails.setAuthorities("ROLE_ADMIN");
            oauthClientDetails.setAutoApprove(true);
            oauthClientDetails.setRefreshTokenValidity(9000);
            oauthClientDetails.setResourceIds("api");
            oauthClientDetails.setScope("read,write");
            oauthClientDetailsRepo.save(oauthClientDetails);*/

            //======For UI (prod)
            OauthClientDetails oauthClientDetails = new OauthClientDetails();
            oauthClientDetails.setClientId("ui-34783822");
            oauthClientDetails.setClientSecret(bCryptPasswordEncoder.encode("538mdue832"));
            oauthClientDetails.setAccessTokenValidity(900);
            oauthClientDetails.setAuthorizedGrantTypes("password,authorization_code,refresh_token");
            oauthClientDetails.setAuthorities("ROLE_USER");
            oauthClientDetails.setAutoApprove(true);
            oauthClientDetails.setRefreshTokenValidity(9000);
            oauthClientDetails.setResourceIds("api");
            oauthClientDetails.setScope("read,write");
            oauthClientDetailsRepo.save(oauthClientDetails);

            //For Admin
            OauthClientDetails oauthClientDetails2 = new OauthClientDetails();
            oauthClientDetails2.setClientId("admin-34283852");
            oauthClientDetails2.setClientSecret(bCryptPasswordEncoder.encode("2483nmed932"));
            oauthClientDetails2.setAccessTokenValidity(900);
            oauthClientDetails2.setAuthorizedGrantTypes("password,authorization_code,refresh_token");
            oauthClientDetails2.setAuthorities("ROLE_ADMIN");
            oauthClientDetails2.setAutoApprove(true);
            oauthClientDetails2.setRefreshTokenValidity(9000);
            oauthClientDetails2.setResourceIds("api");
            oauthClientDetails2.setScope("read,write");
            oauthClientDetailsRepo.save(oauthClientDetails2);
       // }
    }

    @Override
    public void run(String... args){
        //populate database
        populateDb();
    }

    public static void main(String args[]){
        BCryptPasswordEncoder bCryptPasswordEncoders = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoders.encode("7034S7LU5"));
    }
}