package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

/**
 * 
 * @author gzapalaglio
 *
 */
public class AgendamentoExameVO implements Serializable {

	
	private static final long serialVersionUID = 919403018697944956L;
	private AelItemSolicitacaoExames itemExame;
	private AelItemSolicitacaoExames itemExameOriginal;
	private Boolean selecionado;
	private Date dthrReativacao;
	private AghUnidadesFuncionais unidadeExecutora;
	private Date dthrAgenda;
	private String dthrAgendaString;
	private String diaSemanaData;
	private List<AelAmostras> listaAmostras;
	
	
	public AgendamentoExameVO() {
	}
	
	public AelItemSolicitacaoExames getItemExame() {
		return itemExame;
	}

	public void setItemExame(AelItemSolicitacaoExames itemExame) {
		this.itemExame = itemExame;
	}

	public AelItemSolicitacaoExames getItemExameOriginal() {
		return itemExameOriginal;
	}

	public void setItemExameOriginal(AelItemSolicitacaoExames itemExameOriginal) {
		this.itemExameOriginal = itemExameOriginal;
	}

	public Date getDthrAgenda() {
		return dthrAgenda;
	}

	public void setDthrAgenda(Date dthrAgenda) {
		this.dthrAgenda = dthrAgenda;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}
	
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setDthrReativacao(Date dthrReativacao) {
		this.dthrReativacao = dthrReativacao;
	}

	public Date getDthrReativacao() {
		return dthrReativacao;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public String getDthrAgendaString() {
		return dthrAgendaString;
	}

	public void setDthrAgendaString(String dthrAgendaString) {
		this.dthrAgendaString = dthrAgendaString;
	}

	public String getDiaSemanaData() {
		return diaSemanaData;
	}

	public void setDiaSemanaData(String diaSemanaData) {
		this.diaSemanaData = diaSemanaData;
	}

	public List<AelAmostras> getListaAmostras() {
		return listaAmostras;
	}

	public void setListaAmostras(List<AelAmostras> listaAmostras) {
		this.listaAmostras = listaAmostras;
	}
	
	 
}
