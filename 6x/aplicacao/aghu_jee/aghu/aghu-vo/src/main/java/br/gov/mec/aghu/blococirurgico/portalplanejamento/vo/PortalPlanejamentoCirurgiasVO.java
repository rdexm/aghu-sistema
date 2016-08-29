package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PortalPlanejamentoCirurgiasVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2946757276982542165L;

	private static final Log LOG = LogFactory.getLog(PortalPlanejamentoCirurgiasVO.class);
	
	private static final String TIME_FORMAT = "HH:mm";
	
	private Short sala;
	private String turno; 
	private Date horaInicio;
	private Date horaFim;
	private String cedencia;
	
	public PortalPlanejamentoCirurgiasVO() {
	}

	public PortalPlanejamentoCirurgiasVO(Short sala) {
		super();
		this.sala = sala;
	}

	public Short getSala() {
		return sala;
	}
	
	public void setSala(Short sala) {
		this.sala = sala;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
		try {
			if(this.horaInicio != null) {
				this.horaInicio = DateUtils.parseDate(DateUtil.dataToString(horaInicio, TIME_FORMAT), new String[] {TIME_FORMAT});
			}
		} catch (ParseException e) {
			LOG.error(horaInicio, e);
		}
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
		try {
			if(horaFim != null) {
				this.horaFim = DateUtils.parseDate(DateUtil.dataToString(horaFim, TIME_FORMAT), new String[] {TIME_FORMAT});
			}
		} catch (ParseException e) {
			LOG.error(horaFim, e);
		}
	}
	
	public String getCedencia() {
		return cedencia;
	}

	public void setCedencia(String cedencia) {
		this.cedencia = cedencia;
	}

	public boolean isCedencia() {
		return cedencia != null ? DominioSimNao.valueOf(cedencia).isSim() : false;
	}

	public enum Fields {
		SALA("sala"),
		TURNO("turno"),
		HORA_INICIO("horaInicio"),
		HORA_FIM("horaFim"),
		CEDENCIA("cedencia"); 

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sala == null) ? 0 : sala.hashCode());
		result = prime * result + ((turno == null) ? 0 : turno.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PortalPlanejamentoCirurgiasVO)) {
			return false;
		}
		PortalPlanejamentoCirurgiasVO other = (PortalPlanejamentoCirurgiasVO) obj;
		if (sala == null) {
			if (other.sala != null) {
				return false;
			}
		} else if (!sala.equals(other.sala)) {
			return false;
		}
		if (turno == null) {
			if (other.turno != null) {
				return false;
			}
		} else if (!turno.equals(other.turno)) {
			return false;
		}
		return true;
	}
}
