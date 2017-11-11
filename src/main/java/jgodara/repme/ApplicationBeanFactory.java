package jgodara.repme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jgodara.repme.businessaction.VouchesBA;
import jgodara.repme.businessaction.VouchpostBA;
import jgodara.repme.businessobject.VouchesBO;
import jgodara.repme.businessobject.VouchpostBO;
import jgodara.repme.steam.businessaction.SteamClientBA;
import jgodara.repme.steam.businessobject.SteamClientBO;

@Configuration
public class ApplicationBeanFactory {
	
	@Bean
	public SteamClientBA steamClientBA() {
		SteamClientBA steamClientBA = new SteamClientBA();
		steamClientBA.setSteamClientBO(steamClientBO());
		return steamClientBA;
	}
	
	@Bean
	public SteamClientBO steamClientBO() {
		SteamClientBO steamClientBO = new SteamClientBO();
		return steamClientBO;
	}
	
	@Bean
	public VouchesBA vouchesBA() {
		VouchesBA vouchesBA = new VouchesBA();
		vouchesBA.setVouchesBO(vouchesBO());
		return vouchesBA;
	}
	
	@Bean
	public VouchpostBA vouchpstBA() {
		VouchpostBA vouchpostBA = new VouchpostBA();
		vouchpostBA.setVouchpostBO(vouchpostBO());
		return vouchpostBA;
	}
	
	@Bean
	public VouchpostBO vouchpostBO() {
		return new VouchpostBO();
	}
	
	@Bean
	public VouchesBO vouchesBO() {
		return new VouchesBO();
	}

}
