package jgodara.repme.businessaction;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import jgodara.repme.businessobject.VouchesBO;
import jgodara.repme.datatransfer.VouchesDTO;
import jgodara.repme.model.Vouches;
import jgodara.repme.steam.businessaction.SteamClientBA;

public class VouchesBA {
	
	private static final Logger logger = Logger.getLogger(VouchesBA.class);

	@Autowired
	private VouchesBO vouchesBO;
	
	@Autowired
	private SteamClientBA steamClientBA;

	public List<VouchesDTO> getRecentVouces() {
		List<VouchesDTO> dto = new ArrayList<VouchesDTO>();
		for (Vouches vouch : vouchesBO.getRecentVouches()) {
			dto.add(vouchesDoToDto(vouch));
		}
		return dto;
	}
	
	public VouchesDTO getVouchPost(Long vouchId) {
		Vouches vouch = vouchesBO.getVouchByVouchId(vouchId);
		return vouchesDoToDto(vouch);
	}
	
	private VouchesDTO vouchesDoToDto(Vouches vouch) {
		VouchesDTO vouchesDTO = new VouchesDTO();
		try {
			vouchesDTO.setAmount(vouch.getAmount());
			vouchesDTO.setEvictor(steamClientBA.getSteamUser(vouch.getEvictid()));
			vouchesDTO.setOwner(steamClientBA.getSteamUser(vouch.getOwnerid()));
			vouchesDTO.setUom(vouch.getUom());
			vouchesDTO.setVouchid(vouch.getVouchid());
			vouchesDTO.setCreateTime(vouch.getCreateTime());
		} catch (Exception ex) {
			logger.error("Cannot prepare VouchDTO...", ex);
		}
		return vouchesDTO;
	}

	public void setVouchesBO(VouchesBO vouchesBO) {
		this.vouchesBO = vouchesBO;
	}

}
