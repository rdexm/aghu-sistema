package br.gov.mec.aghu.sicon.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioSortableSitEnvioContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ContratoGridVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class RescisaoIntegracaoSiconController extends ActionController {

	private static final long serialVersionUID = -4371723665342595513L;

	@EJB
	private ISiconFacade siconFacade;
	
	private List<ContratoGridVO> rescisoes;  // Grid da aba de Contratos
	
	private ContratoGridVO rescSelecionada;
	private Integer seqRescisaoSelecionada;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	
	/**
	 * Método que executa a pesquisa DEFAULT inicial para ABA de RESCISOES
	 */
	public void pesquisaInicialRescisoes(DominioSituacaoEnvioContrato situacao) throws BaseException{
		
		limpar();
		
		List<ScoResContrato> rescisoesContratos = siconFacade.listarRescisoesSituacaoEnvio(situacao);
		
		if(rescisoesContratos != null &&
		   rescisoesContratos.size() > 0){
				   
			rescisoes = setDadosVO(rescisoesContratos);
		}
	}
	
	
	/**
	 * Pesquisa as rescisões dos contratos retornados na primeira consulta
	 */
	public void pesquisarRescisoes(List<ContratoGridVO> listContratosVO){
	
		limpar();
		
		List<ScoContrato> listContratos = new ArrayList<ScoContrato>(); 
			
		for(ContratoGridVO contratoVO : listContratosVO){
			if(contratoVO.getContrato() != null){
				listContratos.add(contratoVO.getContrato());
			}
		}
		
		// Consulta as rescisões através da lista de contratos extraída 
		//da listagem da pesquisa PRINCIPAL de contratos
		List<ScoResContrato> rescisoesContratos = siconFacade.listarRescisoesContrato(listContratos);
		
		if(rescisoesContratos != null &&
		   rescisoesContratos.size() > 0){
				   
			rescisoes = setDadosVO(rescisoesContratos);
		}
		
	}
	
	/**
	 * Pesquisa as rescisões dos contratos
	 */
	public void pesquisarRescisoes(List<ContratoGridVO> listContratosVO,
			                       ContratoFiltroVO filtroContratoIntegracao){
	
		limpar();
		
		List<ScoContrato> listContratos = new ArrayList<ScoContrato>(); 
			
		for(ContratoGridVO contratoVO : listContratosVO){
			if(contratoVO.getContrato() != null){
				listContratos.add(contratoVO.getContrato());
			}
		}
		
		// Consulta as rescisões através da lista de contratos extraída 
		//da listagem da pesquisa PRINCIPAL de contratos
		List<ScoResContrato> rescisoesContratos = siconFacade.listarRescisoesContratoFiltro(listContratos, filtroContratoIntegracao);
		
		if(rescisoesContratos != null &&
		   rescisoesContratos.size() > 0){
				   
			rescisoes = setDadosVO(rescisoesContratos);
		}
		
	}
	
	/**
	 * Pesquisa as rescisões dos contratos
	 */
	public void pesquisarRescisao(ContratoFiltroVO filtroContratoIntegracao){
	
		limpar();
		
		// Consulta as rescisões através da lista de contratos extraída 
		//da listagem da pesquisa PRINCIPAL de contratos
		List<ScoResContrato> rescisoesContratos = siconFacade.pesquisarRescisoes(filtroContratoIntegracao);
		
		if(rescisoesContratos != null &&
		   rescisoesContratos.size() > 0){
				   
			rescisoes = setDadosVO(rescisoesContratos);
		}
		
	}
	
	
	
	private List<ContratoGridVO> setDadosVO(List<ScoResContrato> rescisoesContratos){
		
		List<ContratoGridVO> grid = new ArrayList<ContratoGridVO>();
		
		//Popula VO utilizado na Grid
		for(ScoResContrato rescisao: rescisoesContratos){
			
			ContratoGridVO vo = new ContratoGridVO(rescisao);
			
			if(rescisao.getIndSituacao() == DominioSituacaoEnvioContrato.A || rescisao.getIndSituacao() == DominioSituacaoEnvioContrato.AR ){
				vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
				vo.setPendenciasTooltip("Rescisão aguardando envio para o SICON.");
			
			}else if(rescisao.getIndSituacao() == DominioSituacaoEnvioContrato.E){
				vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
				vo.setPendenciasTooltip("Rescisão enviada com sucesso para o SICON.");
				
			}else if(rescisao.getIndSituacao() == DominioSituacaoEnvioContrato.EE){
				vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
				vo.setPendenciasTooltip("Erro(s) no envio da rescisão do contrato para o SICON.");
			}
			
			grid.add(vo);
		}
		
		return grid;
	}
	
	
	public void limpar(){
		
		setRescSelecionada(null);
		
		if(rescisoes != null){
			rescisoes.clear();
		}else{
			rescisoes = new ArrayList<ContratoGridVO>();
		}
		
	}
	
		
	//Getters and Setters
	public List<ContratoGridVO> getRescisoes() {
		return rescisoes;
	}

	public void setRescisoes(List<ContratoGridVO> rescisoes) {
		this.rescisoes = rescisoes;
	}
	
	
	//Getters and Setters para Rescisão Selecionada por Radio Button
	
	public ContratoGridVO getRescSelecionada() {
		return rescSelecionada;
	}

	public void setRescSelecionada(ContratoGridVO rescSelecionada) {
		this.rescSelecionada = rescSelecionada;
	}

	public Integer getSeqRescisaoSelecionada() {
		return seqRescisaoSelecionada;
	}

	public void setSeqRescisaoSelecionada(Integer seqRescisaoSelecionada) {
		this.seqRescisaoSelecionada = seqRescisaoSelecionada;
		setRescSelecionada();
	}
	
	private void setRescSelecionada(){
		
		if(getSeqRescisaoSelecionada() != null){
					
			for(ContratoGridVO rescVO : rescisoes){
				
				if(rescVO.getResContrato() != null &&
				   rescVO.getResContrato().getSeq() != null && 
				   rescVO.getResContrato().getSeq().equals(this.getSeqRescisaoSelecionada())) {
					
					setRescSelecionada(rescVO);
					
					break;
				}
			}
		
		}else{
			setRescSelecionada(null);
		}
	}
	
}
