package br.gov.mec.aghu.estoque.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrderBy;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirRequisicaoMaterialController;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialVO;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EfetivarRequisicaoMaterialController extends ActionController {
	
	private static final long serialVersionUID = -2637057510813289846L;
	private static final Log LOG = LogFactory.getLog(EfetivarRequisicaoMaterialController.class);
	private static final String PESQUISAR_MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA = "estoque-pesquisarMaterialBloqueioDesbloqueioProblema";
	private static final String MATERIAL_BLOQUEIO_DESBLOQUEIO = "estoque-materialBloqueioDesbloqueio";
	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";
	private static final String GERACAO_REQUISICAO_MATERIAL = "estoque-geracaoRequisicaoMaterial";
	private static final String PESQUISA_REQUISICAO_MATERIAL_EFETIVAR = "estoque-pesquisaRequisicaoMaterialEfetivar";
	
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	// Parâmetros da conversação
	private Integer rmsSeq;
	private String voltarPara = "pesquisaRequisicaoMaterialEfetivar"; // O padrão é voltar para interface de pesquisa
	
	// Instância da requisição de material
	private SceReqMaterial requisicaoMaterial;
	
	// Instância da requisição de material
	private RequisicaoMaterialVO vo;

	// Instância da lista de itens de requisição de material
	private List<SceItemRms> listaItensRequisicaoMaterial = new ArrayList<SceItemRms>();

	// Controla o item de requisição de material selecionado na listagem do XHTML
	private SceItemRms itemRequisicaoMaterialSelecionado = new SceItemRms();
	
	@Inject
	private ImprimirRequisicaoMaterialController imprimirRequisicaoMaterialController;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 


		if (this.rmsSeq != null){

			// Resgata a instância da requisição de material do banco através do parâmetro de conversação
			this.requisicaoMaterial = this.estoqueFacade.obterRequisicaoMaterial(this.rmsSeq);

			if (this.requisicaoMaterial != null) {
				
				vo = new RequisicaoMaterialVO();
				
				vo.setSeq(requisicaoMaterial.getSeq());
				vo.setIndSituacao(requisicaoMaterial.getIndSituacao());
				vo.setEstorno(requisicaoMaterial.getEstorno());
				vo.setIndImpresso(requisicaoMaterial.getIndImpresso());
				vo.setAlmoxarifado(requisicaoMaterial.getAlmoxarifado());
				vo.setGrupoMaterial(requisicaoMaterial.getGrupoMaterial());
				vo.setCentroCusto(requisicaoMaterial.getCentroCusto());
				vo.setCentroCustoAplica(requisicaoMaterial.getCentroCustoAplica());
			
				// Resgata a lista de itens de requisição de material
				this.listaItensRequisicaoMaterial = this.estoqueFacade.pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(requisicaoMaterial.getSeq(), DominioOrderBy.E, null);
			
				// Seleciona o primeiro item da lista de itens de requisição de material
				if (this.listaItensRequisicaoMaterial != null && !this.listaItensRequisicaoMaterial.isEmpty()){
				
					this.itemRequisicaoMaterialSelecionado = this.listaItensRequisicaoMaterial.get(0);
				}
			}		
		}
	
	}
	
	
	/**
	 * Seleciona e controla um item de resquisição de material na listagem do XHMTL
	 * @param itemRms
	 */
	public void selecionarItemRequisicaoMaterial(SceItemRms itemRms){
		// Seta uma referencia do item selecionado da lista de solicitacoes de coleta
		this.itemRequisicaoMaterialSelecionado = itemRms;
	}
	
	/**
	 * Verifica se a requisição de material está efetivada
	 * @return
	 */
	public boolean isRequisicaoMaterialEfetivada(){
		if(this.vo!=null){
			return DominioSituacaoRequisicaoMaterial.E.equals(this.vo.getIndSituacao());
		}
		return false;
	}
	
	/**
	 * Efetiva um item de requisição de material
	 */
	public void efetivar(){
		
		this.requisicaoMaterial = this.estoqueFacade.obterRequisicaoMaterial(vo.getSeq());
		vo.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
			
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {			
			for (SceItemRms sceItemRms : this.listaItensRequisicaoMaterial) {
				
				this.estoqueBeanFacade.gravarItensRequisicaoMaterial(sceItemRms, nomeMicrocomputador);
				
			}
			
			// Grava a requisição de material
			this.estoqueBeanFacade.efetivarRequisicaoMaterial(this.requisicaoMaterial, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EFETIVAR_REQUISICAO_MATERIAL");
		
			if (requisicaoMaterial.getAlmoxarifado().getIndCentral()) {
				
				directPrint();
				
			}
			this.listaItensRequisicaoMaterial = this.estoqueFacade.pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(requisicaoMaterial.getSeq(), DominioOrderBy.E, null);
			
		} catch (BaseException e) {
			//  Seta a situação original da requisição do material
			vo.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
			super.apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error("Erro",e);
		} catch (SystemException e) {
			LOG.error("Erro",e);
		} catch (IOException e) {
			LOG.error("Erro",e);
		}
	}
	
	/**
	 * Impressão Direta
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() throws BaseException, JRException, SystemException, IOException{
		imprimirRequisicaoMaterialController.setNumeroRM(requisicaoMaterial.getSeq());
		imprimirRequisicaoMaterialController.setDuasVias(DominioSimNao.N);
		imprimirRequisicaoMaterialController.directPrint();
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		if(this.voltarPara != null){
			if(voltarPara.equals("gerarRequisicaoMaterial")){
				return GERACAO_REQUISICAO_MATERIAL;
			}if(voltarPara.equals("pesquisaRequisicaoMaterialEfetivar")){
				return PESQUISA_REQUISICAO_MATERIAL_EFETIVAR;
			}
		}
		return null;
	}
	
	
	public String pesquisarEstoqueAlmoxarifado(){
		return PESQUISAR_ESTOQUE_ALMOXARIFADO;
	}
	
	public String materialBloqueioDesbloqueio(){
		return MATERIAL_BLOQUEIO_DESBLOQUEIO;
	}
	
	
	public String pesquisarMaterialBloqueioDesbloqueioProblema(){
		return PESQUISAR_MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA;
	}
	
	/*
	 * Getters e setters
	 */

	public Integer getRmsSeq() {
		return rmsSeq;
	}

	public void setRmsSeq(Integer rmsSeq) {
		this.rmsSeq = rmsSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public SceReqMaterial getRequisicaoMaterial() {
		return requisicaoMaterial;
	}

	public void setRequisicaoMaterial(SceReqMaterial requisicaoMaterial) {
		this.requisicaoMaterial = requisicaoMaterial;
	}

	public List<SceItemRms> getListaItensRequisicaoMaterial() {
		return listaItensRequisicaoMaterial;
	}

	public void setListaItensRequisicaoMaterial(
			List<SceItemRms> listaItensRequisicaoMaterial) {
		this.listaItensRequisicaoMaterial = listaItensRequisicaoMaterial;
	}

	public SceItemRms getItemRequisicaoMaterialSelecionado() {
		return itemRequisicaoMaterialSelecionado;
	}

	public void setItemRequisicaoMaterialSelecionado(SceItemRms itemRequisicaoMaterialSelecionado) {
		this.itemRequisicaoMaterialSelecionado = itemRequisicaoMaterialSelecionado;
	}

	public RequisicaoMaterialVO getVo() {
		return vo;
	}

	public void setVo(RequisicaoMaterialVO vo) {
		this.vo = vo;
	}

}