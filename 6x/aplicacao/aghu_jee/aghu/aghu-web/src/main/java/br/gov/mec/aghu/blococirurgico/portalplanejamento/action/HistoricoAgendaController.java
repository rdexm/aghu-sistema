package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.HistoricoAgendaVO;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class HistoricoAgendaController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8287224641042083597L;
		
	private String titulo;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private List<HistoricoAgendaVO> listaHistoricoAgenda;
	private HistoricoAgendaVO selecao;
	private Integer seqp;
	private String descricaoHistorico;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void limparHistoricoAgenda(){
		this.setListaHistoricoAgenda(new ArrayList<HistoricoAgendaVO>());
	}
	
	/**
	 * abrevia strings(nome, descrição) para apresentação na tela
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	
	public String informadoPor(String str){
		String informadoPor = null;
		if(str != null){
			informadoPor = "Informado por " + str;
		}
		return informadoPor;
	}
	
	
	public void informarHistoricoAgenda(Integer agdSeq){
		this.buscarHistoricoAgenda(agdSeq);
		obterHistoricoAgenda(agdSeq);
	}
	
	public Integer buscarHistoricoAgenda(Integer agdSeq){
		limparHistoricoAgenda();
		MbcAgendas agendas = blocoCirurgicoPortalPlanejamentoFacade.buscarAgenda(agdSeq);
		StringBuilder titulo = new StringBuilder();
		
		titulo.append(getBundle().getString("TITLE_TITULO_HISTORICO_AGENDA"));
		if(agendas != null) {
			titulo.append(' ').append(agendas.getPaciente().getNome()); 
			titulo.append(" - ").append(CoreUtil.formataProntuario(agendas.getPaciente().getProntuario()));
		}
		this.setTitulo(titulo.toString());
		
		return agdSeq;
	}
	
	public void obterHistoricoAgenda(Integer agdSeq) {
		listaHistoricoAgenda = blocoCirurgicoFacade.buscarAgendaHistoricos(agdSeq);

		if(listaHistoricoAgenda.size() > 0) {
			seqp = 0;
			this.selecao = listaHistoricoAgenda.get(0);
			selecionarItem();
		}
	}
	
	public void selecionarItem() {
		for(HistoricoAgendaVO hist : listaHistoricoAgenda) {
			if(hist.getSeqp().equals(seqp)) {
				this.descricaoHistorico = hist.getOcorrencia();
				this.selecao = hist;
				break;
			}
		}
	}

	public List<HistoricoAgendaVO> getListaHistoricoAgenda() {
		return listaHistoricoAgenda;
	}

	public void setListaHistoricoAgenda(List<HistoricoAgendaVO> listaHistoricoAgenda) {
		this.listaHistoricoAgenda = listaHistoricoAgenda;
	}
	

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public String getDescricaoHistorico() {
		return descricaoHistorico;
	}

	public void setDescricaoHistorico(String descricaoHistorico) {
		this.descricaoHistorico = descricaoHistorico;
	}

	public HistoricoAgendaVO getSelecao() {
		return selecao;
	}

	public void setSelecao(HistoricoAgendaVO selecao) {
		this.selecao = selecao;
		this.seqp = this.selecao.getSeqp();
		this.selecionarItem();
	}
}