package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioMotivoEncerramentoNotificacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class NotificacaoTopografiasVO implements BaseBean {

	private static final long serialVersionUID = -344574843809676385L;
	
	private Integer seq;
	private Integer seqAtendimento;
	private Integer codigoPaciente;
	private String codigoEtiologiaInfeccao;
	private Short seqTopografiaProcedimento;
	private String descricaoTopografiaProcedimento;
	private Date criadoEm;
	private Date dataInicio;
	private DominioConfirmacaoCCI confirmacaoCci;
	private Integer servidorMatricula;
	private Short servidorVinCodigo;
	private Integer servidorEncerradoMatricula;
	private Short servidorEncerradoVinCodigo;
	private Integer servidorMatriculaConfirmado;
	private Short servidorVinCodigoConfirmado;
	private Short seqUnidadesFuncional;
	private Short seqUnidadeFuncionalNotificada;
	private Short numeroQuarto;
	private Short quartoNotificado;
	private String leito;
	private String leitoNotificado;
	private Date dataFim;
	private DominioMotivoEncerramentoNotificacao motivoEncerramento;
	private Integer seqInstituicoaHospitalar;
	private Long rniPnnSeq;
	private Short rniSeqp;
	
	// SERVICOS
	private Short seqUnidadeFuncionaAtendimento;
	private Short numeroQuartoAtendimento;
	private Date dataHoraInicioAtendimento;
	private Integer numeroConsulta;
	private Integer seqAtendimentosUrgencia;
	private Integer seqInternacao;
	private Integer seqHospitaisDia;

	// dados da dataTable
	private Date dataAtendimento;
	private String strSeqDescricaoTopoProced;
	private String localizacao;
	private DominioIndContaminacao potencialContaminacao;
	private Integer podDcgCrgSeq;
	private Short podDcgSeqp;
	private Integer podSeqp;
	private String descricaoProcedimento;
	private Date dtHrInicioCrg;
	private String procedDataContaminacao;
	
	private ProcedRealizadoVO procedimento;
	private Integer procedimentoCirurgico;
	
	private Integer seqMedidadPreventiva;

	public NotificacaoTopografiasVO() {

	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getCodigoEtiologiaInfeccao() {
		return codigoEtiologiaInfeccao;
	}

	public void setCodigoEtiologiaInfeccao(String codigoEtiologiaInfeccao) {
		this.codigoEtiologiaInfeccao = codigoEtiologiaInfeccao;
	}

	public Short getSeqTopografiaProcedimento() {
		return seqTopografiaProcedimento;
	}

	public void setSeqTopografiaProcedimento(Short seqTopografiaProcedimento) {
		this.seqTopografiaProcedimento = seqTopografiaProcedimento;
	}
	
	public String getDescricaoTopografiaProcedimento() {
		return descricaoTopografiaProcedimento;
	}

	public void setDescricaoTopografiaProcedimento(String descricaoTopografiaProcedimento) {
		this.descricaoTopografiaProcedimento = descricaoTopografiaProcedimento;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public DominioConfirmacaoCCI getConfirmacaoCci() {
		return confirmacaoCci;
	}

	public void setConfirmacaoCci(DominioConfirmacaoCCI confirmacaoCci) {
		this.confirmacaoCci = confirmacaoCci;
	}

	public Integer getServidorMatricula() {
		return servidorMatricula;
	}

	public void setServidorMatricula(Integer servidorMatricula) {
		this.servidorMatricula = servidorMatricula;
	}

	public Short getServidorVinCodigo() {
		return servidorVinCodigo;
	}

	public void setServidorVinCodigo(Short servidorVinCodigo) {
		this.servidorVinCodigo = servidorVinCodigo;
	}

	public Integer getServidorEncerradoMatricula() {
		return servidorEncerradoMatricula;
	}

	public void setServidorEncerradoMatricula(Integer servidorEncerradoMatricula) {
		this.servidorEncerradoMatricula = servidorEncerradoMatricula;
	}

	public Short getServidorEncerradoVinCodigo() {
		return servidorEncerradoVinCodigo;
	}

	public void setServidorEncerradoVinCodigo(Short servidorEncerradoVinCodigo) {
		this.servidorEncerradoVinCodigo = servidorEncerradoVinCodigo;
	}

	public Integer getServidorMatriculaConfirmado() {
		return servidorMatriculaConfirmado;
	}

	public void setServidorMatriculaConfirmado(
			Integer servidorMatriculaConfirmado) {
		this.servidorMatriculaConfirmado = servidorMatriculaConfirmado;
	}

	public Short getServidorVinCodigoConfirmado() {
		return servidorVinCodigoConfirmado;
	}

	public void setServidorVinCodigoConfirmado(Short servidorVinCodigoConfirmado) {
		this.servidorVinCodigoConfirmado = servidorVinCodigoConfirmado;
	}

	public Short getSeqUnidadesFuncional() {
		return seqUnidadesFuncional;
	}

	public void setSeqUnidadesFuncional(Short seqUnidadesFuncional) {
		this.seqUnidadesFuncional = seqUnidadesFuncional;
	}

	public Short getSeqUnidadeFuncionalNotificada() {
		return seqUnidadeFuncionalNotificada;
	}

	public void setSeqUnidadeFuncionalNotificada(
			Short seqUnidadeFuncionalNotificada) {
		this.seqUnidadeFuncionalNotificada = seqUnidadeFuncionalNotificada;
	}

	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	public Short getQuartoNotificado() {
		return quartoNotificado;
	}

	public void setQuartoNotificado(Short quartoNotificado) {
		this.quartoNotificado = quartoNotificado;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getLeitoNotificado() {
		return leitoNotificado;
	}

	public void setLeitoNotificado(String leitoNotificado) {
		this.leitoNotificado = leitoNotificado;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public DominioMotivoEncerramentoNotificacao getMotivoEncerramento() {
		return motivoEncerramento;
	}

	public void setMotivoEncerramento(DominioMotivoEncerramentoNotificacao motivoEncerramento) {
		this.motivoEncerramento = motivoEncerramento;
	}

	public Integer getSeqInstituicoaHospitalar() {
		return seqInstituicoaHospitalar;
	}

	public void setSeqInstituicoaHospitalar(Integer seqInstituicoaHospitalar) {
		this.seqInstituicoaHospitalar = seqInstituicoaHospitalar;
	}

	public Long getRniPnnSeq() {
		return rniPnnSeq;
	}

	public void setRniPnnSeq(Long rniPnnSeq) {
		this.rniPnnSeq = rniPnnSeq;
	}

	public Short getRniSeqp() {
		return rniSeqp;
	}

	public void setRniSeqp(Short rniSeqp) {
		this.rniSeqp = rniSeqp;
	}
	
	public Short getSeqUnidadeFuncionaAtendimento() {
		return seqUnidadeFuncionaAtendimento;
	}

	public void setSeqUnidadeFuncionaAtendimento(Short seqUnidadeFuncionaAtendimento) {
		this.seqUnidadeFuncionaAtendimento = seqUnidadeFuncionaAtendimento;
	}

	public Short getNumeroQuartoAtendimento() {
		return numeroQuartoAtendimento;
	}

	public void setNumeroQuartoAtendimento(Short numeroQuartoAtendimento) {
		this.numeroQuartoAtendimento = numeroQuartoAtendimento;
	}

	public Date getDataHoraInicioAtendimento() {
		return dataHoraInicioAtendimento;
	}

	public void setDataHoraInicioAtendimento(Date dataHoraInicioAtendimento) {
		this.dataHoraInicioAtendimento = dataHoraInicioAtendimento;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public Integer getSeqAtendimentosUrgencia() {
		return seqAtendimentosUrgencia;
	}

	public void setSeqAtendimentosUrgencia(Integer seqAtendimentosUrgencia) {
		this.seqAtendimentosUrgencia = seqAtendimentosUrgencia;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public Integer getSeqHospitaisDia() {
		return seqHospitaisDia;
	}

	public void setSeqHospitaisDia(Integer seqHospitaisDia) {
		this.seqHospitaisDia = seqHospitaisDia;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	
	public String getStrSeqDescricaoTopoProced() {
		return strSeqDescricaoTopoProced;
	}

	public void setStrSeqDescricaoTopoProced(String strSeqDescricaoTopoProced) {
		this.strSeqDescricaoTopoProced = strSeqDescricaoTopoProced;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	
	public DominioIndContaminacao getPotencialContaminacao() {
		return potencialContaminacao;
	}

	public void setPotencialContaminacao(
			DominioIndContaminacao potencialContaminacao) {
		this.potencialContaminacao = potencialContaminacao;
	}

	public Integer getPodDcgCrgSeq() {
		return podDcgCrgSeq;
	}

	public void setPodDcgCrgSeq(Integer podDcgCrgSeq) {
		this.podDcgCrgSeq = podDcgCrgSeq;
	}

	public Short getPodDcgSeqp() {
		return podDcgSeqp;
	}

	public void setPodDcgSeqp(Short podDcgSeqp) {
		this.podDcgSeqp = podDcgSeqp;
	}

	public Integer getPodSeqp() {
		return podSeqp;
	}

	public void setPodSeqp(Integer podSeqp) {
		this.podSeqp = podSeqp;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public Date getDtHrInicioCrg() {
		return dtHrInicioCrg;
	}

	public void setDtHrInicioCrg(Date dtHrInicioCrg) {
		this.dtHrInicioCrg = dtHrInicioCrg;
	}

	public String getProcedDataContaminacao() {
		return procedDataContaminacao;
	}

	public void setProcedDataContaminacao(String procedDataContaminacao) {
		this.procedDataContaminacao = procedDataContaminacao;
	}

	public ProcedRealizadoVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedRealizadoVO procedimento) {
		this.procedimento = procedimento;
	}

	public Integer getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(Integer procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public Integer getSeqMedidadPreventiva() {
		return seqMedidadPreventiva;
	}

	public void setSeqMedidadPreventiva(Integer seqMedidadPreventiva) {
		this.seqMedidadPreventiva = seqMedidadPreventiva;
	}

	public enum Fields {
		SEQ("seq"),
		SEQ_ATENDIMENTO("seqAtendimento"),
		CODIGO_PACIENTE("codigoPaciente"),
		CODIGO_ETIOLOGIA_INFECCAO("codigoEtiologiaInfeccao"),
		SEQ_TOPOGRAFIA_PROCEDIMENTO("seqTopografiaProcedimento"),
		DESCRICAO_TOPOGRAFIA_PROCEDIMENTO("descricaoTopografiaProcedimento"),
		CRIADO_EM("criadoEm"),
		DATA_INICIO("dataInicio"),
		CONFIRMACAO_CCI("confirmacaoCci"),
		SERVIDOR_MATRICULA("servidorMatricula"),
		SERVIDOR_VIN_CODIGO("servidorVinCodigo"),
		SERVIDOR_ENCERRADO_MATRICULA("servidorEncerradoMatricula"),
		SERVIDOR_ENCERRADO_VIN_CODIGO("servidorEncerradoVinCodigo"),
		SERVIDOR_MATRICULA_CONFIRMADO("servidorMatriculaConfirmado"),
		SERVIDOR_VIN_CODIGO_CONFIRMADO("servidorVinCodigoConfirmado"),
		SEQ_UNIDADE_FUNCIONAL("seqUnidadesFuncional"),
		SEQ_UNIDADE_FUNCIONAL_NOTIFICADA("seqUnidadeFuncionalNotificada"),
		NUMERO_QUARTO("numeroQuarto"),
		NUMERO_QUARTO_NOTIFICADO("quartoNotificado"),
		LEITO("leito"),
		LEITO_NOTIFICADO("leitoNotificado"),
		DATA_FIM("dataFim"),
		MOTIVO_ENCERRAMENTO("motivoEncerramento"),
		SEQ_INSTITUICOA_HOSPITALAR("seqInstituicoaHospitalar"),
		RNI_PNN_SEQ("rniPnnSeq"),
		RNI_SEQP("rniSeqp"),
		SEQ_UNIDADE_FUNCIONA_ATENDIMENTO("seqUnidadeFuncionaAtendimento"),
		NUMERO_QUARTO_ATENDIMENTO("numeroQuartoAtendimento"),
		DTHR_INICIO_ATENDIMENTO("dataHoraInicioAtendimento"),
		NUMERO_CONSULTA("numeroConsulta"),
		SEQ_ATENDIMENTO_URGENCIA("seqAtendimentosUrgencia"),
		SEQ_INTERNACAO("seqInternacao"),
		SEQ_HOSPITAIS_DIA("seqHospitaisDia"),
		SEQ_MEDIDAD_PREVENTIVA("seqMedidadPreventiva"),
		IND_CONTAMINACAO("potencialContaminacao"),
		POD_DCG_CRG_SEQ("podDcgCrgSeq"),
		POD_DCG_SEQP("podDcgSeqp"),
		POD_SEQP("podSeqp"),
		DESCRICAO_PROCED("descricaoProcedimento"),
		DTHR_INICIO_CIRG("dtHrInicioCrg")
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		NotificacaoTopografiasVO other = (NotificacaoTopografiasVO) obj;
		
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
