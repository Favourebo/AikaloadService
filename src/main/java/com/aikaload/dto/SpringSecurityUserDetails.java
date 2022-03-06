package com.aikaload.dto;

import java.util.Collection;

import com.aikaload.entity.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Data
public class SpringSecurityUserDetails extends User{
	private Long id;
    private String companyName;
    private String contactPersonName;
	private UserRole userRole;
	private boolean isAccountVerified;
	private boolean isAccountInformationUpdated;

	
	public SpringSecurityUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities,Long id,String companyName,String contactPersonName,boolean isAccountVerified,boolean isAccountInformationUpdated) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	    this.companyName = companyName;
	    this.contactPersonName = contactPersonName;
	    this.id = id;
	    this.isAccountVerified = isAccountVerified;
	    this.isAccountInformationUpdated = isAccountInformationUpdated;
	}

}
