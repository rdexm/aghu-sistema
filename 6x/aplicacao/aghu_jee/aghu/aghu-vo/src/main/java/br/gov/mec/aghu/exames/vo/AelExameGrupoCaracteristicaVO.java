package br.gov.mec.aghu.exames.vo;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AelExameGrupoCaracteristicaVO implements BaseBean {

	private static final long serialVersionUID = 8190380276309229473L;
	
	private static final Log LOG = LogFactory.getLog(AelExameGrupoCaracteristicaVO.class);

	private AelExameGrupoCaracteristica aelExameGrupoCaracteristica = new AelExameGrupoCaracteristica();

	private Boolean emEdicao = false;

	
	
	public AelExameGrupoCaracteristicaVO() {
		super();
	}
	

	public AelExameGrupoCaracteristicaVO(
			AelExameGrupoCaracteristica aelExameGrupoCaracteristica,
			Boolean emEdicao) {
		super();
		this.aelExameGrupoCaracteristica = aelExameGrupoCaracteristica;
		this.emEdicao = emEdicao;
	}

	public AelExameGrupoCaracteristicaVO(AelExameGrupoCaracteristicaVO item) {
		AelExameGrupoCaracteristica exGrupoCarac = null;
		try {
			exGrupoCarac = (AelExameGrupoCaracteristica) BeanUtils.cloneBean(item.getAelExameGrupoCaracteristica());
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage());
		} catch (InstantiationException e) {
			LOG.error(e.getMessage());
		} catch (InvocationTargetException e) {
			LOG.error(e.getMessage());
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage());
		}
		
		this.setAelExameGrupoCaracteristica(exGrupoCarac);
		this.setEmEdicao(item.getEmEdicao());
	}


	public AelExameGrupoCaracteristica getAelExameGrupoCaracteristica() {
		return aelExameGrupoCaracteristica;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setAelExameGrupoCaracteristica(
			AelExameGrupoCaracteristica aelExameGrupoCaracteristica) {
		this.aelExameGrupoCaracteristica = aelExameGrupoCaracteristica;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

}
