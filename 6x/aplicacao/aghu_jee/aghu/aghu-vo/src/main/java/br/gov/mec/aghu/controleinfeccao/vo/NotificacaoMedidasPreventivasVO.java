package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.dominio.DominioMotivoEncerramentoNotificacao;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.DateUtil;

public class NotificacaoMedidasPreventivasVO implements BaseBean, Comparable<NotificacaoMedidasPreventivasVO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2309649271248351689L;

	
	
//	SELECT MMP.SEQ,
	private Integer seq;
//	  MMP.SER_MATRICULA,
	private Integer serMatricula;	
//	  MMP.SER_VIN_CODIGO,
	private Short serVinCodigo;
//	  MMP.UNF_SEQ,
	private Short unfSeq;	
//	  MMP.UNF_SEQ_NOTIFICADO,
	private Short unfSeqNotificado; 
//	  MMP.PAC_CODIGO,
	private Integer pacCodigo;
//	  MMP.IND_CONFIRMACAO_CCI,
	private DominioConfirmacaoCCI indConfirmacaoCCI;
//	  MMP.DT_INICIO,
	private Date dtInicio;
//	  MMP.CRIADO_EM,
	private Date criadoEm;
//	  MMP.EIN_TIPO,
	private String einTipo;	
//	  MMP.PAI_SEQ,
	private Integer paiSeq;
//	  MMP.MIT_SEQ,
	private Integer mitSeq;
//	  MMP.SER_MATRICULA_CONFIRMADO,
	private Integer serMatriculaConfirmado;
//	  MMP.SER_VIN_CODIGO_CONFIRMADO,
	private Short serVinCodigoConfirmado;
//	  MMP.ATD_SEQ
	private Integer atdSeq;
//	  MMP.IHO_SEQ,
	private Byte ihoSeq;
//	  MMP.QRT_NUMERO,
	private Short qrtNumero;	
//	  MMP.QRT_NUMERO_NOTIFICADO,
	private Short qrtNumeroNotificado;	
//	  MMP.LTO_LTO_ID,
	private String ltoId; 
//	  MMP.LTO_LTO_ID_NOTIFICADO,
	private String ltoIdNotificado;
//	  MMP.SER_MATRICULA_ENCERRADO,
	private Integer serMatriculaEncerrado;
//	  MMP.SER_VIN_CODIGO_ENCERRADO,
	private Short serVinCodigoEncerrado;
//	  MMP.DT_FIM,
	private Date dtFim;
//	  MMP.MOTIVO_ENCERRAMENTO,
	private DominioMotivoEncerramentoNotificacao motivoEncerramento;
//	  MMP.IND_IMPRESSAO,
	private Boolean indImpressao;
//	  MMP.CIF_SEQ,
	private Integer cifSeq;
//	  MMP.IND_GMR,
	private Boolean indGmr;	
//	  MMP.IND_ISOLAMENTO,
	private Boolean indIsolamento;
//	  MIT.top_seq,
	private Short topSeq;
//	  TOP.descricao,
	private String topDescricao;
//	  ein.descricao,
	private String einDescricao;
// 	pai.ind_uso_quarto_privativo
	private Boolean usoQuartoPrivativo;
//  pai.descricao	
	private String descricaoPatologia;

//	 <Serviço: Consulta>,
	private Integer consulta;
//	 <Serviço: Localização: Andar-Ind_Ala-Sigla-Leito-Quarto>
	private String localizacao;
	
	private String leitoAtendimento;
	private Short quartoAtendimento;
	private Short unfAtendimento;
	
//	 <Serviço: Data Atendimento>
	private Date dtAtendimento;
	private Date dtInternacao;
	private Date dtConsulta;
	private Date dtAtendimentoUrgencia;
	private Date dtHospitaisDia;
	
	private String descricao;
	private String descricaoSeqPatologia;
	private String descricaoSeqTopografia;
	

	public enum Fields {
		SEQ("seq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		UNF_SEQ("unfSeq"),
		UNF_SEQ_NOTIFICADO("unfSeqNotificado"),
		PAC_CODIGO("pacCodigo"),
		IND_CONFIRMACAO_CCI("indConfirmacaoCCI"),
		DT_INICIO("dtInicio"),
		CRIADO_EM("criadoEm"),
		EIN_TIPO("einTipo"),
		LOCALIZACAO("localizacao"),
		CONSULTA("consulta"),
		EIN_DESCRICAO("einDescricao"),
		TOP_DESCRICAO("topDescricao"),
		TOP_SEQ("topSeq"),
		IND_ISOLAMENTO("indIsolamento"),
		CIF_SEQ("cifSeq"),
		IND_IMPRESSAO("indImpressao"),
		MOTIVO_ENCERRAMENTO("motivoEncerramento"),
		DT_FIM("dtFim"),
		SER_MATRICULA_ENCERRADO("serMatriculaEncerrado"),
		SER_VIN_CODIGO_ENCERRADO("serVinCodigoEncerrado"),		
		SER_MATRICULA_CONFIRMADO("serMatriculaConfirmado"),
		SER_VIN_CODIGO_CONFIRMADO("serVinCodigoConfirmado"),
		PAI_SEQ("paiSeq"),
		MIT_SEQ("mitSeq"),
		ATD_SEQ("atdSeq"),
		IHO_SEQ("ihoSeq"),
		QRT_NUMERO("qrtNumero"),
		QRT_NUMERO_NOTIFICADO("qrtNumeroNotificado"),
		LTO_LTO_ID("ltoId"),
		LTO_LTO_ID_NOTIFICADO("ltoIdNotificado"),
		IND_GMR("indGmr"),
		ATENDIMENTO_DATA_CONSULTA("dtConsulta"),
		URGENCIA_DATA_ATENDIMENTO("dtAtendimentoUrgencia"),
		HOSPITAL_DIA_DATA_INICIO("dtHospitaisDia"),
		INTERNACAO_DATA_INICIO("dtInternacao"),
		LEITO_ATENDIMENTO("leitoAtendimento"),
		QUARTO_ATENDIMENTO("quartoAtendimento"),
		UNF_ATENDIMENTO("unfAtendimento"),
		USO_QUARTO_PRIVATIVO("usoQuartoPrivativo"),
		DESCRICAO_PATOLOGIA("descricaoPatologia"),
		DESCRICAO("descricao"),
		DT_ATENDIMENTO("dtAtendimento")
		;
 		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSeq() {
		return seq;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public Short getUnfSeqNotificado() {
		return unfSeqNotificado;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public DominioConfirmacaoCCI getIndConfirmacaoCCI() {
		return indConfirmacaoCCI;
	}
	public Date getDtInicio() {
		return dtInicio;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public String getEinTipo() {
		return einTipo;
	}
	public Integer getPaiSeq() {
		return paiSeq;
	}
	public Integer getMitSeq() {
		return mitSeq;
	}
	public Integer getSerMatriculaConfirmado() {
		return serMatriculaConfirmado;
	}
	public Short getSerVinCodigoConfirmado() {
		return serVinCodigoConfirmado;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public Byte getIhoSeq() {
		return ihoSeq;
	}
	public Short getQrtNumero() {
		return qrtNumero;
	}
	public Short getQrtNumeroNotificado() {
		return qrtNumeroNotificado;
	}
	public String getLtoId() {
		return ltoId;
	}
	public String getLtoIdNotificado() {
		return ltoIdNotificado;
	}
	public Integer getSerMatriculaEncerrado() {
		return serMatriculaEncerrado;
	}
	public Short getSerVinCodigoEncerrado() {
		return serVinCodigoEncerrado;
	}
	public Date getDtFim() {
		return dtFim;
	}
	public DominioMotivoEncerramentoNotificacao getMotivoEncerramento() {
		return motivoEncerramento;
	}
	public Boolean getIndImpressao() {
		return indImpressao;
	}
	public Integer getCifSeq() {
		return cifSeq;
	}
	public Boolean getIndGmr() {
		return indGmr;
	}
	public Boolean getIndIsolamento() {
		return indIsolamento;
	}
	public Short getTopSeq() {
		return topSeq;
	}
	public String getTopDescricao() {
		return topDescricao;
	}
	public String getEinDescricao() {
		return einDescricao;
	}
	public Date getDtAtendimento() {
		return dtAtendimento;
	}
	public Integer getConsulta() {
		return consulta;
	}

	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public void setUnfSeqNotificado(Short unfSeqNotificado) {
		this.unfSeqNotificado = unfSeqNotificado;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public void setIndConfirmacaoCCI(DominioConfirmacaoCCI indConfirmacaoCCI) {
		this.indConfirmacaoCCI = indConfirmacaoCCI;
	}
	public void setDtIncio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public void setEinTipo(String einTipo) {
		this.einTipo = einTipo;
	}
	public void setPaiSeq(Integer paiSeq) {
		this.paiSeq = paiSeq;
	}
	public void setMitSeq(Integer mitSeq) {
		this.mitSeq = mitSeq;
	}
	public void setSerMatriculaConfirmado(Integer serMatriculaConfirmado) {
		this.serMatriculaConfirmado = serMatriculaConfirmado;
	}
	public void setSerVinCodigoConfirmado(Short serVinCodigoConfirmado) {
		this.serVinCodigoConfirmado = serVinCodigoConfirmado;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public void setIhoSeq(Byte ihoSeq) {
		this.ihoSeq = ihoSeq;
	}
	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}
	public void setQrtNumeroNotificado(Short qrtNumeroNotificado) {
		this.qrtNumeroNotificado = qrtNumeroNotificado;
	}
	public void setLtoId(String ltoId) {
		this.ltoId = ltoId;
	}
	public void setLtoIdNotificado(String ltoIdNotificado) {
		this.ltoIdNotificado = ltoIdNotificado;
	}
	public void setSerMatriculaEncerrado(Integer serMatriculaEncerrado) {
		this.serMatriculaEncerrado = serMatriculaEncerrado;
	}
	public void setSerVinCodigoEncerrado(Short serVinCodigoEncerrado) {
		this.serVinCodigoEncerrado = serVinCodigoEncerrado;
	}
	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	public void setMotivoEncerramento(DominioMotivoEncerramentoNotificacao motivoEncerramento) {
		this.motivoEncerramento = motivoEncerramento;
	}
	public void setIndImpressao(Boolean indImpressao) {
		this.indImpressao = indImpressao;
	}
	public void setCifSeq(Integer cifSeq) {
		this.cifSeq = cifSeq;
	}
	public void setIndGmr(Boolean indGmr) {
		this.indGmr = indGmr;
	}
	public void setIndIsolamento(Boolean indIsolamento) {
		this.indIsolamento = indIsolamento;
	}
	public void setTopSeq(Short topSeq) {
		this.topSeq = topSeq;
	}
	public void setTopDescricao(String topDescricao) {
		this.topDescricao = topDescricao;
	}
	public void setEinDescricao(String einDescricao) {
		this.einDescricao = einDescricao;
	}
	public void setDtAtendimento(Date dtAtendimento) {
		this.dtAtendimento = dtAtendimento;
	}
	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}
	public Date getDtInternacao() {
		return dtInternacao;
	}
	public void setDtInternacao(Date dtInternacao) {
		this.dtInternacao = dtInternacao;
	}
	public Date getDtConsulta() {
		return dtConsulta;
	}
	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}
	public Date getDtAtendimentoUrgencia() {
		return dtAtendimentoUrgencia;
	}
	public void setDtAtendimentoUrgencia(Date dtAtendimentoUrgencia) {
		this.dtAtendimentoUrgencia = dtAtendimentoUrgencia;
	}
	public Date getDtHospitaisDia() {
		return dtHospitaisDia;
	}
	public void setDtHospitaisDia(Date dtHospitaisDia) {
		this.dtHospitaisDia = dtHospitaisDia;
	}
	public String getLeitoAtendimento() {
		return leitoAtendimento;
	}
	public Short getQuartoAtendimento() {
		return quartoAtendimento;
	}
	public Short getUnfAtendimento() {
		return unfAtendimento;
	}
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public void setLeitoAtendimento(String leitoAtendimento) {
		this.leitoAtendimento = leitoAtendimento;
	}
	public void setQuartoAtendimento(Short quartoAtendimento) {
		this.quartoAtendimento = quartoAtendimento;
	}
	public void setUnfAtendimento(Short unfAtendimento) {
		this.unfAtendimento = unfAtendimento;
	}

	@Override
	public int compareTo(NotificacaoMedidasPreventivasVO o) {
		if(this.dtAtendimento == null &&  o.getDtAtendimento() == null) {
			return 0;
		} else if (this.dtAtendimento == null && o.getDtAtendimento() != null) {
			return 1;
		} else if (this.dtAtendimento != null && o.getDtAtendimento() == null) {
			return -1;
		}
		return this.dtAtendimento.compareTo(o.getDtAtendimento());
	}
	public Boolean getUsoQuartoPrivativo() {
		return usoQuartoPrivativo;
	}
	public void setUsoQuartoPrivativo(Boolean usoQuartoPrivativo) {
		this.usoQuartoPrivativo = usoQuartoPrivativo;
	}
	public String getDescricaoPatologia() {
		return descricaoPatologia;
	}
	public void setDescricaoPatologia(String descricaoPatologia) {
		this.descricaoPatologia = descricaoPatologia;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricaoSeqPatologia() {
		return descricaoSeqPatologia;
	}
	public void setDescricaoSeqPatologia(String descricaoSeqPatologia) {
		this.descricaoSeqPatologia = descricaoSeqPatologia;
	}
	public String getDescricaoSeqTopografia() {
		return descricaoSeqTopografia;
	}
	public void setDescricaoSeqTopografia(String descricaoSeqTopografia) {
		this.descricaoSeqTopografia = descricaoSeqTopografia;
	}
	
	 @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NotificacaoMedidasPreventivasVO)) {
            return false;
        }
        NotificacaoMedidasPreventivasVO other = (NotificacaoMedidasPreventivasVO) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(DateUtil.truncaData(this.getDtInicio()), DateUtil.truncaData(other.getDtInicio()));
        umEqualsBuilder.append(this.getTopSeq(), other.getTopSeq());
        umEqualsBuilder.append(this.getAtdSeq(), other.getAtdSeq());
        umEqualsBuilder.append(this.getPaiSeq(), other.getPaiSeq());
        return umEqualsBuilder.isEquals();
       }

}
