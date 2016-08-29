package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioSortableSitEnvioContrato;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ContratoGridVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe para o Controller da aba de Contratos de Integração com Sicon
 * 
 * @author agerling
 *
 */


public class ContratoIntegracaoSiconController extends ActionController {

	private static final long serialVersionUID = -6190342938453683625L;

	@EJB
	private ISiconFacade siconFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private List<ContratoGridVO> contratos;  // Grid da aba de Contratos
	
	private ContratoGridVO contSelecionado;
	private String contSelecionadoNro;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

 
	/**
	 * Método que executa a pesquisa PRINCIPAL para a tabela da ABA de CONTRATOS
	 * 
	 * @param ContratoFiltroVO - Filtro populado na tela principal
	 */
	public void pesquisarContratos(ContratoFiltroVO filtroContratoIntegracao) throws BaseException{
		
		limpar();
	
		List<ScoContrato> listContratos = new ArrayList<ScoContrato>();
		
		listContratos = siconFacade.listarContratosFiltro(filtroContratoIntegracao);
		
		if(listContratos != null &&
		   listContratos.size() > 0){
			
			contratos = setDadosVO(listContratos);
		}
	}
		
	public void limpar(){
		
		setContSelecionado(null);
		
		if(contratos != null){
			contratos.clear();
		}else{
			contratos = new ArrayList<ContratoGridVO>();
		}
		
	}
	
	private List<ContratoGridVO> setDadosVO(List<ScoContrato> listContratos) throws BaseException{
		
		List<ContratoGridVO> grid = new ArrayList<ContratoGridVO>();
		
		AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERC_CONTROLE_VLR_CTR);
		
		//Popula VO utilizado na Grid
		for(ScoContrato contrato: listContratos){
			
			ContratoGridVO vo = new ContratoGridVO(contrato);
			
			BigDecimal valor = new BigDecimal(param.getVlrTexto());
			
			if(contrato.getIndOrigem() == DominioOrigemContrato.A){
				vo.setFlagType(checkValEfetivado(contrato, valor));
				
				//adicionando texto da tooltip para flags
				// 1 = flag laranja
				// 2 = flag vermelha
				if (vo.getFlagType() == 1) {
					StringBuilder valorFlag = new StringBuilder();
					
					valorFlag.append(getBundle().getString("MENSAGEM_TOOLTIP_FLAG_LARANJA1"))
					.append(' ');
					double percentual = contrato.getValEfetAfs().doubleValue() / contrato.getValorTotal().doubleValue();
					NumberFormat nf = NumberFormat.getPercentInstance();
					nf.setMaximumFractionDigits(3);
					nf.setMinimumFractionDigits(1);
					valorFlag.append(nf.format(percentual))
					.append(' ')
					.append(getBundle().getString("MENSAGEM_TOOLTIP_FLAG_LARANJA2"));
					vo.setFlagTooltip(valorFlag.toString());
				} else if (vo.getFlagType() == 2) {
					vo.setFlagTooltip(getBundle().getString("MENSAGEM_TOOLTIP_FLAG_VERMELHA"));
				}
			}
			
			if(contrato.getSituacao() == DominioSituacaoEnvioContrato.A || contrato.getSituacao() == DominioSituacaoEnvioContrato.AR ){
				vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
				vo.setPendenciasTooltip("Contrato aguardando envio para o SICON.");
			
			}else if(contrato.getSituacao() == DominioSituacaoEnvioContrato.E){
				vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
				vo.setPendenciasTooltip("Contrato enviado com sucesso para o SICON.");
				
			}else if(contrato.getSituacao() == DominioSituacaoEnvioContrato.EE){
				vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
				vo.setPendenciasTooltip("Erro(s) no envio do contrato para o SICON.");
			}
			
			grid.add(vo);
		}
		
		return grid;
		
	}
	
	private int checkValEfetivado(ScoContrato contrato, BigDecimal vlrLim){
		
		BigDecimal valEfet = contrato.getValEfetAfs();
		BigDecimal valTmp = contrato.getValorTotal().subtract(contrato.getValorTotal().multiply(vlrLim));
		
		if(valEfet != null){
			if(valEfet.compareTo(contrato.getValorTotal()) == 1 ||  valEfet.compareTo(contrato.getValorTotal())==0){
				return 2;
			}
			
			if(valEfet.compareTo(valTmp) >=0 && valEfet.compareTo(contrato.getValorTotal()) <0 ){
				return 1;
			}
		}
		
		return 0;
	}
	
	//Getters and Setters
	public List<ContratoGridVO> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoGridVO> contratos) {
		this.contratos = contratos;
	}
	
		
	//Getters e Setters para Contrato Selecionado por Radio Button
	
	public ContratoGridVO getContSelecionado() {
		return contSelecionado;
	}

	public void setContSelecionado(ContratoGridVO contSelecionado) {
		this.contSelecionado = contSelecionado;
	}

	public String getContSelecionadoNro() {
		return contSelecionadoNro;
	}

	public void setContSelecionadoNro(String contSelecionadoNro) {
		this.contSelecionadoNro = contSelecionadoNro;
		setContSelecionado();
	}
	
	private void setContSelecionado(){
		
		if(getContSelecionadoNro() != null){
			
			for(ContratoGridVO contVO : this.contratos){
				
				if (contVO.getContrato() != null && 
					contVO.getContrato().getNrContrato() != null &&
					contVO.getContrato().getNrContrato().toString().equals(this.getContSelecionadoNro())){
					
					setContSelecionado(contVO);
					
					break;
				}
			}
			
		}else{
			setContSelecionado(null);
		}
	}
				
}
