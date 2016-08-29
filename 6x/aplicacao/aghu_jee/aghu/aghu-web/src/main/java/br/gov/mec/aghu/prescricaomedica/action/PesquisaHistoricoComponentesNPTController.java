package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.primefaces.event.SelectEvent;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.core.action.ActionController;

public class PesquisaHistoricoComponentesNPTController extends ActionController {
	private static final long serialVersionUID = -1846518679437964237L;
	private List<ComponenteNPTVO> listaHistorico;
	private ComponenteNPTVO itemSelecionado;
	
	private Integer seqComponente;
	
	private static final String REDIRECIONA_COMPONENTES_NPT = "pesquisaComponentesNPT";

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	public void inicio() {
		itemSelecionado = new ComponenteNPTVO();
		pesquisarConsultorInternacao();
	}
	
	public void selecionarItem(SelectEvent e) {
		this.itemSelecionado = (ComponenteNPTVO) e.getObject();
	}
	
	public void pesquisarConsultorInternacao() {
		listaHistorico = farmaciaFacade.pesquisarHistoricoComponenteNPT(seqComponente);
		if (listaHistorico != null && listaHistorico.size() > 0) {
			itemSelecionado = listaHistorico.get(0);
		}
	}
	public ComponenteNPTVO getItemSelecionado() {
		return itemSelecionado;
	}
	public void setItemSelecionado(ComponenteNPTVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	public List<ComponenteNPTVO> getListaHistorico() {
		return listaHistorico;
	}
	public void setListaHistorico(List<ComponenteNPTVO> listaHistorico) {
		this.listaHistorico = listaHistorico;
	}
	public Integer getSeqComponente() {
		return seqComponente;
	}
	public void setSeqComponente(Integer seqComponente) {
		this.seqComponente = seqComponente;
	}
	public String cancelar() {
		return REDIRECIONA_COMPONENTES_NPT;
	}
}
