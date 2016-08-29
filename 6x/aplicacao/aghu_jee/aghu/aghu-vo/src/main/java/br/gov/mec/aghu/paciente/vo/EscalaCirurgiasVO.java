package br.gov.mec.aghu.paciente.vo;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Os dados armazenados nesse objeto representam as escalas de cirurgias utilizadas na geração de relatório.
 * 
 * @author lalegre
 */

public class EscalaCirurgiasVO implements Comparable<EscalaCirurgiasVO> {

	private String sciSeqp;
	private String dthrInicioCirg;
	private String dthrFimCirg;
	private String cspCnvCodigo;
	private String nome;
	private String dtNascimento;
	private String origemPacCirg;
	private String prontuario;
	private String prontuario1;
	private String prntAtivo;
	private String indPrecaucaoEspecial;
	private String seq;
	private String pacCodigo;
	private String volumes;
	private String criadoEm;
	private String quarto;
	private String procedimentoCirurgico;
	private String tipoAnestesia;
	private String nomeCirurgiao;
	private String matricula;
	private String codigo;
	private String serVinCodigo;
	private String totalCirurgias;
	private Date dataInicial;
	private Date dataFinal;
	private String titulo;
	private String contaminacao;
	private String digitaNotaSala;
	private String numeroAgenda;
	private String unfSeq;
	private Integer idade;
	private String convenio;
	private String ladoCirurgia;
	private String diagnostico;
	private String orteseProt;
	private String qtdSolic; // Quantidade de material solicitada
	private String codProced; // Código do procedimento cirúrgico
	private String descricaoPci; // Descrição procedimento cirúrgico
	private String equipamento;
	private String sangue;
	private String quantidade; // Quantidade sangue
	private String exame; // Descrição exame
	private String nomeAnp; // Anestesista professor
	private String material;
	private Date dthrInicioOrdem; // Este campo é utilizado na ordenação dos relatórios de escala
	private String regime; // Regime do procedimento Cirurgico SUS
	private boolean pacienteNotifGMR; // Determina se o paciente é portador de GMR
	private String procedimentoCompletoEscala; // Descrição completa do procedimento na escala de cirurgias aghu
	private List<SubRelatorioEscalaCirurgiasOrteseProteseVO> subRelatorioEscalaCirurgiasOrteseProtese;
	private List<SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO> subRelatorioOPMEOrteseProtese;
	private List<EscalaCirurgiasVO> escalaCirurgiasVO;
	private String observacao;
	private String observacaoTitle;

	public String getSciSeqp() {
		return sciSeqp;
	}

	public void setSciSeqp(String sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public String getDthrInicioCirg() {
		return dthrInicioCirg;
	}

	public void setDthrInicioCirg(String dthrInicioCirg) {
		this.dthrInicioCirg = dthrInicioCirg;
	}

	public String getDthrFimCirg() {
		return dthrFimCirg;
	}

	public void setDthrFimCirg(String dthrFimCirg) {
		this.dthrFimCirg = dthrFimCirg;
	}

	public String getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(String cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getOrigemPacCirg() {
		return origemPacCirg;
	}

	public void setOrigemPacCirg(String origemPacCirg) {
		this.origemPacCirg = origemPacCirg;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getPrntAtivo() {
		return prntAtivo;
	}

	public void setPrntAtivo(String prntAtivo) {
		this.prntAtivo = prntAtivo;
	}

	public String getIndPrecaucaoEspecial() {
		return indPrecaucaoEspecial;
	}

	public void setIndPrecaucaoEspecial(String indPrecaucaoEspecial) {
		this.indPrecaucaoEspecial = indPrecaucaoEspecial;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(String pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getVolumes() {
		return volumes;
	}

	public void setVolumes(String volumes) {
		this.volumes = volumes;
	}

	public String getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getQuarto() {
		return quarto;
	}

	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}

	public String getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(String procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public String getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(String tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public String getNomeCirurgiao() {
		return nomeCirurgiao;
	}

	public void setNomeCirurgiao(String nomeCirurgiao) {
		this.nomeCirurgiao = nomeCirurgiao;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(String serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getTotalCirurgias() {
		return totalCirurgias;
	}

	public void setTotalCirurgias(String totalCirurgias) {
		this.totalCirurgias = totalCirurgias;
	}

	public String getProntuario1() {
		return prontuario1;
	}

	public void setProntuario1(String prontuario1) {
		this.prontuario1 = prontuario1;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getContaminacao() {
		return contaminacao;
	}

	public void setContaminacao(String contaminacao) {
		this.contaminacao = contaminacao;
	}

	public String getDigitaNotaSala() {
		return digitaNotaSala;
	}

	public void setDigitaNotaSala(String digitaNotaSala) {
		this.digitaNotaSala = digitaNotaSala;
	}

	public String getNumeroAgenda() {
		return numeroAgenda;
	}

	public void setNumeroAgenda(String numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	public String getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(String unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getLadoCirurgia() {
		return ladoCirurgia;
	}

	public void setLadoCirurgia(String ladoCirurgia) {
		this.ladoCirurgia = ladoCirurgia;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getOrteseProt() {
		return orteseProt;
	}

	public void setOrteseProt(String orteseProt) {
		this.orteseProt = orteseProt;
	}

	public String getQtdSolic() {
		return qtdSolic;
	}

	public void setQtdSolic(String qtdSolic) {
		this.qtdSolic = qtdSolic;
	}

	public String getCodProced() {
		return codProced;
	}

	public void setCodProced(String codProced) {
		this.codProced = codProced;
	}

	public String getDescricaoPci() {
		return descricaoPci;
	}

	public void setDescricaoPci(String descricaoPci) {
		this.descricaoPci = descricaoPci;
	}

	public String getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	public String getSangue() {
		return sangue;
	}

	public void setSangue(String sangue) {
		this.sangue = sangue;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getNomeAnp() {
		return nomeAnp;
	}

	public void setNomeAnp(String nomeAnp) {
		this.nomeAnp = nomeAnp;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Date getDthrInicioOrdem() {
		return dthrInicioOrdem;
	}

	public void setDthrInicioOrdem(Date dthrInicioOrdem) {
		this.dthrInicioOrdem = dthrInicioOrdem;
	}

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}
	
	public boolean isPacienteNotifGMR() {
		return pacienteNotifGMR;
	}
	
	public void setPacienteNotifGMR(boolean pacienteNotifGMR) {
		this.pacienteNotifGMR = pacienteNotifGMR;
	}
	
	public String getProcedimentoCompletoEscala() {
		return procedimentoCompletoEscala;
	}
	
	public void setProcedimentoCompletoEscala(String procedimentoCompletoEscala) {
		this.procedimentoCompletoEscala = procedimentoCompletoEscala;
	}

	public List<SubRelatorioEscalaCirurgiasOrteseProteseVO> getSubRelatorioEscalaCirurgiasOrteseProtese() {
		return subRelatorioEscalaCirurgiasOrteseProtese;
	}

	public void setSubRelatorioEscalaCirurgiasOrteseProtese(List<SubRelatorioEscalaCirurgiasOrteseProteseVO> subRelatorioEscalaCirurgiasOrteseProtese) {
		this.subRelatorioEscalaCirurgiasOrteseProtese = subRelatorioEscalaCirurgiasOrteseProtese;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getObservacaoTitle() {
		return observacaoTitle;
	}

	public void setObservacaoTitle(String observacaoTitle) {
		this.observacaoTitle = observacaoTitle;
	}

	@Override
	public int compareTo(EscalaCirurgiasVO o) {
		// Compara sala cirúrgica
		int result = this.getSciSeqp().compareTo(o.getSciSeqp());
		if (result == 0) {
			// Equivalente à instrução NVL(CRG.DTHR_INICIO_CIRG, CRG.DTHR_INICIO_ORDEM)
			if (this.getDthrInicioCirg() != null && !this.getDthrInicioCirg().equals("")) {
				// Compara data inicial da cirúrgia
				result = this.getDthrInicioCirg().compareTo(o.getDthrInicioCirg());
			} else if (this.getDthrInicioOrdem() != null) {
				// Compara data do ínicio da ordem
				result = this.getDthrInicioOrdem().compareTo(o.getDthrInicioOrdem());
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.numeroAgenda);
		return hashCodeBuilder.toHashCode();
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

		EscalaCirurgiasVO other = (EscalaCirurgiasVO) obj;

		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.numeroAgenda, other.numeroAgenda);

		return umEqualsBuilder.isEquals();
	}
	
	public List<EscalaCirurgiasVO> getEscalaCirurgiasVO() {
		return escalaCirurgiasVO;
	}

	public void setEscalaCirurgiasVO(List<EscalaCirurgiasVO> escalaCirurgiasVO) {
		this.escalaCirurgiasVO = escalaCirurgiasVO;
	}

	public List<SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO> getSubRelatorioOPMEOrteseProtese() {
		return subRelatorioOPMEOrteseProtese;
	}

	public void setSubRelatorioOPMEOrteseProtese(
			List<SubRelatorioEscalaCirurgiasOrteseProteseOpmeVO> subRelatorioOPMEOrteseProtese) {
		this.subRelatorioOPMEOrteseProtese = subRelatorioOPMEOrteseProtese;
	}

}
