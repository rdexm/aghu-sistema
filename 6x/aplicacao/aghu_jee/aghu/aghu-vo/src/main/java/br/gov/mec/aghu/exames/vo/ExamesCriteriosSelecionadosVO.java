package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

public class ExamesCriteriosSelecionadosVO extends BaseEntityId<AelItemSolicitacaoExamesId> {

	private static final long serialVersionUID = 8279937166083265110L;

	private AelItemSolicitacaoExamesId id;
	private AelItemSolicitacaoExames aelItemSolicitacaoExames;
	private String solicitacao;
	private String convenio;
	private String prontuario;
	private String paciente;
	private String nome;
	private String unid;
	private String exame;
	private String sit;
	private String urg;
	private String o2;
	private String transporte;

	@Override
	public AelItemSolicitacaoExamesId getId() {
		return id;
	}

	@Override
	public void setId(AelItemSolicitacaoExamesId id) {
		this.id = id;
	}

	public AelItemSolicitacaoExames getAelItemSolicitacaoExames() {
		return aelItemSolicitacaoExames;
	}

	public void setAelItemSolicitacaoExames(AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		this.aelItemSolicitacaoExames = aelItemSolicitacaoExames;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getSit() {
		return sit;
	}

	public void setSit(String sit) {
		this.sit = sit;
	}

	public String getUrg() {
		return urg;
	}

	public void setUrg(String urg) {
		this.urg = urg;
	}

	public String getO2() {
		return o2;
	}

	public void setO2(String o2) {
		this.o2 = o2;
	}

	public String getTransporte() {
		return transporte;
	}

	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}

}
