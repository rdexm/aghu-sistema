package br.gov.mec.aghu.blococirurgico.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.faturamento.vo.DadosConciliacaoVO;
import br.gov.mec.aghu.faturamento.vo.DadosMateriaisConciliacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.StringUtil;


public class ConciliacaoMateriaisController extends	ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = -8502623876437237549L;
	
	@EJB
	private IComprasFacade comprasFacade;

	private Integer seq;
	
	private List<DadosConciliacaoVO> lista = new ArrayList<DadosConciliacaoVO>();
	
	private DadosMateriaisConciliacaoVO dadosForm;
	
	private String justificativa;
	
	private final String PAGE_MATERIAL_CIRURGIA_LIST ="materialPorCirurgiaList";

	public void inicio() {
		
		lista = comprasFacade.pesquisarDadosConcilicacao(seq);
		if(lista.size() > 0){
			for (DadosConciliacaoVO elem : lista) {
				if(elem.getJustificativa() != null && !elem.getJustificativa().isEmpty()){
					elem.setJustificativaBoolean("Sim");
				}
				if(elem.getCodENomeMat().length() > 70){
					elem.setCodENomeMatTrunc(StringUtil.trunc(elem.getCodENomeMat(), true, 70l));
				}else{
					elem.setCodENomeMatTrunc(elem.getCodENomeMat());
				}
			}
		}
		List<DadosMateriaisConciliacaoVO> dadosList = new ArrayList<DadosMateriaisConciliacaoVO>();
		dadosList = comprasFacade.pesquisarDadosMaterialConcilicacao(seq);
		if(dadosList.size() > 0){
			dadosForm = dadosList.get(0);
		}
	}
	
	public void selecionar(DadosConciliacaoVO item){
		justificativa = item.getJustificativa();
	}
	
	public String voltar() {	
		return PAGE_MATERIAL_CIRURGIA_LIST;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public List<DadosConciliacaoVO> getLista() {
		return lista;
	}

	public void setLista(List<DadosConciliacaoVO> lista) {
		this.lista = lista;
	}

	public DadosMateriaisConciliacaoVO getDadosForm() {
		return dadosForm;
	}

	public void setDadosForm(DadosMateriaisConciliacaoVO dadosForm) {
		this.dadosForm = dadosForm;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
}
