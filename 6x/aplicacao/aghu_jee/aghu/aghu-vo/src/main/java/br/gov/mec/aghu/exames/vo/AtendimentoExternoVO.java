package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import javax.swing.text.MaskFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;


public class AtendimentoExternoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6094800668120663316L;
	
	private static final Log LOG = LogFactory.getLog(AtendimentoExternoVO.class);
	
	private AghAtendimentosPacExtern atendimentoPacExtern;
	private Integer seq; 
	
	private AipPacientes paciente;
	
	private AghMedicoExterno medicoExterno;
	private FatConvenioSaudePlano convenioPlano;
	private AelLaboratorioExternos laboratorioHemocentro;
	private String codigoDoador;
	private String nomeContato1;
	private Long telefoneContato1;
	private Short dddTelefoneContato1;
	private String nomeContato2;
	private Short dddTelefoneContato2;
	private Long telefoneContato2;
	private Date dataColeta;
	private Boolean emEdicao = Boolean.FALSE;
	
	private String dddFoneContato1;
	private String dddFoneContato2;
	
	private AghAtendimentos atendimento;

	private Boolean atendimentoPermitePrescricaoAmbulatorial;
	
	public AtendimentoExternoVO() {
	}
	
	public AtendimentoExternoVO(AghAtendimentosPacExtern atendPacExternEl) {
		this.atendimentoPacExtern = atendPacExternEl;
		
		this.setSeq(atendPacExternEl.getSeq());
		this.setPaciente(atendPacExternEl.getPaciente());
		this.setCodigoDoador(atendPacExternEl.getCodigoDoador());
		this.setConvenioPlano(atendPacExternEl.getConvenioSaudePlano());
		this.setDataColeta(atendPacExternEl.getDtColeta());
		this.setLaboratorioHemocentro(atendPacExternEl.getLaboratorioExterno());
		this.setMedicoExterno(atendPacExternEl.getMedicoExterno());
		this.setNomeContato1(atendPacExternEl.getContato1());
		this.setNomeContato2(atendPacExternEl.getContato2());
		this.setTelefoneContato1(atendPacExternEl.getFoneContato1());
		this.setDddTelefoneContato1(atendPacExternEl.getDddFoneContato1());
		this.setTelefoneContato2(atendPacExternEl.getFoneContato2());
		this.setDddTelefoneContato2(atendPacExternEl.getDddFoneContato2());
		
		if (atendPacExternEl.getAtendimentos() != null && !atendPacExternEl.getAtendimentos().isEmpty()) {
			this.setAtendimento(atendPacExternEl.getAtendimentos().get(0));
		}
		
		this.setDddFoneContato1(this.makeDddFoneContato(this.getDddTelefoneContato1(), this.getTelefoneContato1()));
		this.setDddFoneContato2(this.makeDddFoneContato(this.getDddTelefoneContato2(), this.getTelefoneContato2()));
		
	}
	
	/*
	public AtendimentoExternoVO(AtendimentoExternoVO vo) {
		this.setPaciente(vo.getPaciente());
		this.setCodigoDoador(vo.getCodigoDoador());
		this.setConvenioPlano(vo.getConvenioPlano());
		this.setDataColeta(vo.getDataColeta());
		this.setLaboratorioHemocentro(vo.getLaboratorioHemocentro());
		this.setMedicoExterno(vo.getMedicoExterno());
		this.setNomeContato1(vo.getNomeContato1());
		this.setNomeContato2(vo.getNomeContato2());
		this.setTelefoneContato1(vo.getTelefoneContato1());
		this.setDddTelefoneContato1(vo.getDddTelefoneContato2());
		this.setTelefoneContato2(vo.getTelefoneContato2());
		this.setDddTelefoneContato2(vo.getDddTelefoneContato2());
		
		this.setAtendimento(vo.getAtendimento());
		
		this.setDddFoneContato1(vo.getDddFoneContato1());
		this.setDddFoneContato2(vo.getDddFoneContato2());
	}
	*/

	/** get/set **/
	public AghMedicoExterno getMedicoExterno() {
		return medicoExterno;
	}

	public void setMedicoExterno(AghMedicoExterno medicoExterno) {
		this.medicoExterno = medicoExterno;
	}

	public FatConvenioSaudePlano getConvenioPlano() {
		return convenioPlano;
	}

	public void setConvenioPlano(FatConvenioSaudePlano c) {
		this.convenioPlano = c;
	}

	public AelLaboratorioExternos getLaboratorioHemocentro() {
		return laboratorioHemocentro;
	}

	public void setLaboratorioHemocentro(
			AelLaboratorioExternos laboratorioHemocentro) {
		this.laboratorioHemocentro = laboratorioHemocentro;
	}

	public String getCodigoDoador() {
		return codigoDoador;
	}

	public void setCodigoDoador(String codigoDoador) {
		this.codigoDoador = codigoDoador;
	}

	public String getNomeContato1() {
		return nomeContato1;
	}

	public void setNomeContato1(String nomeContato1) {
		this.nomeContato1 = nomeContato1;
	}

	public Long getTelefoneContato1() {
		return telefoneContato1;
	}

	public void setTelefoneContato1(Long t) {
		this.telefoneContato1 = t;
	}

	public String getNomeContato2() {
		return nomeContato2;
	}

	public void setNomeContato2(String nomeContato2) {
		this.nomeContato2 = nomeContato2;
	}

	public Long getTelefoneContato2() {
		return telefoneContato2;
	}

	public void setTelefoneContato2(Long t) {
		this.telefoneContato2 = t;
	}

	public Date getDataColeta() {
		return dataColeta;
	}

	public void setDataColeta(Date dataColeta) {
		this.dataColeta = dataColeta;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Short getDddTelefoneContato1() {
		return dddTelefoneContato1;
	}

	public void setDddTelefoneContato1(Short dddTelefoneContato1) {
		this.dddTelefoneContato1 = dddTelefoneContato1;
	}

	public Short getDddTelefoneContato2() {
		return dddTelefoneContato2;
	}

	public void setDddTelefoneContato2(Short dddTelefoneContato2) {
		this.dddTelefoneContato2 = dddTelefoneContato2;
	}
	
	public Boolean getTemContato() {
		Boolean status = Boolean.FALSE;
		if(StringUtils.isNotBlank(this.getNomeContato1()) 
				|| StringUtils.isNotBlank(this.getNomeContato2())
				|| this.getTelefoneContato1() != null
				|| this.getTelefoneContato2() != null) {
			status = Boolean.TRUE;
		}
		return status;
	}


	public Integer getAtendimentoSeq() {
		return this.getAtendimento().getSeq();
	}

	public void setAtendimento(AghAtendimentos atd) {
		this.atendimento = atd;
	}

	public AghAtendimentos getAtendimento() {
		if (this.atendimento == null) {
			this.atendimento = new AghAtendimentos();
		}
		return this.atendimento;
	}

	public AghAtendimentosPacExtern getModel() {
		if (this.atendimentoPacExtern == null) {
			this.atendimentoPacExtern = new AghAtendimentosPacExtern();
		}
		
		this.atendimentoPacExtern.setLaboratorioExterno(this.getLaboratorioHemocentro());
		this.atendimentoPacExtern.setMedicoExterno(this.getMedicoExterno());
		this.atendimentoPacExtern.setPaciente(this.getPaciente());
		this.atendimentoPacExtern.setConvenioSaudePlano(this.getConvenioPlano());
		this.atendimentoPacExtern.setDtColeta(this.getDataColeta());
		this.atendimentoPacExtern.setCodigoDoador(this.getCodigoDoador());
		
		this.atendimentoPacExtern.setContato1(this.getNomeContato1());
		this.atendimentoPacExtern.setDddFoneContato1(this.getDDD(this.getDddFoneContato1()));
		this.atendimentoPacExtern.setFoneContato1(this.getFone(this.getDddFoneContato1()));
		
		this.atendimentoPacExtern.setContato2(this.getNomeContato2());
		this.atendimentoPacExtern.setDddFoneContato2(this.getDDD(this.getDddFoneContato2()));
		this.atendimentoPacExtern.setFoneContato2(this.getFone(this.getDddFoneContato2()));
		
		return this.atendimentoPacExtern;
	}
	
	/**
	 * (99)_9999-9999
	 * @return
	 */
	protected Short getDDD(String numeroComDDD) {
		if(numeroComDDD != null){
			String[] array = numeroComDDD.split(" ");
			String ddd = null;
			
			if (array.length > 1) {
				ddd = array[0];
				ddd = ddd.replace("(", "");
				ddd = ddd.replace(")", "");
			}
			
			return ddd != null ? Short.valueOf(ddd) : null;
			
		} else {
			return null;
		}
	}
	
	/**
	 * (99)_9999-9999
	 * @return
	 */
	protected Long getFone(String numeroComDDD) {
		if(numeroComDDD != null){
			String[] array = numeroComDDD.split(" ");
			String ddd = null;
			
			if (array.length > 1) {
				ddd = array[1];
				ddd = ddd.replace("-", "");
			}
			
			return ddd != null ? Long.valueOf(ddd) : null;
			
		} else {
			return null;
		}
	}
	
	protected String makeDddFoneContato(Short ddd, Long fone) {
		StringBuffer foneConcatenado = new StringBuffer();
		
		if (ddd != null) {
			foneConcatenado.append(ddd.toString());
		}

		if (fone != null) {
			foneConcatenado.append(fone.toString());
		}

		return foneConcatenado.toString();
	}

	public String getDddFoneContatoComMascara1() {
		try {
			if(StringUtils.isNotBlank(this.getDddFoneContato1())) {
				MaskFormatter formata = this.obterMascara();
				return formata.valueToString(this.getDddFoneContato1());
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	public String getDddFoneContatoComMascara2() {
		try {
			if(StringUtils.isNotBlank(this.getDddFoneContato2())) {
				MaskFormatter formata = this.obterMascara();
				return formata.valueToString(this.getDddFoneContato2());
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	private MaskFormatter obterMascara() throws ParseException {
		MaskFormatter formata = new MaskFormatter("(##) ####-####");
		formata.setValueContainsLiteralCharacters(false);
		return formata;
	}
	
	/**
	 * @param paciente the paciente to set
	 */
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	/**
	 * @return the paciente
	 */
	public AipPacientes getPaciente() {
		return paciente;
	}

	/**
	 * @param dddFoneContato1 the dddFoneContato1 to set
	 */
	public void setDddFoneContato1(String dddFoneContato1) {
		this.dddFoneContato1 = dddFoneContato1;
	}

	/**
	 * @return the dddFoneContato1
	 */
	public String getDddFoneContato1() {
		return dddFoneContato1;
	}

	/**
	 * @param dddFoneContato2 the dddFoneContato2 to set
	 */
	public void setDddFoneContato2(String dddFoneContato2) {
		this.dddFoneContato2 = dddFoneContato2;
	}

	/**
	 * @return the dddFoneContato2
	 */
	public String getDddFoneContato2() {
		return dddFoneContato2;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * @return the seq
	 */
	public Integer getSeq() {
		return seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof AtendimentoExternoVO)) {
			return false;
		}
		AtendimentoExternoVO other = (AtendimentoExternoVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	public Boolean getAtendimentoPermitePrescricaoAmbulatorial() {
		return atendimentoPermitePrescricaoAmbulatorial;
	}
	
	public void setAtendimentoPermitePrescricaoAmbulatorial(
			Boolean atendimentoPermitePrescricaoAmbulatorial) {
		this.atendimentoPermitePrescricaoAmbulatorial = atendimentoPermitePrescricaoAmbulatorial;
	}

}
