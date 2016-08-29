package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.AelExameGrupoCaracteristicaVO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AgruparCaracteristicaExameController extends ActionController implements ActionPaginator {

	private static final Log LOG = LogFactory.getLog(AgruparCaracteristicaExameController.class);

	private static final long serialVersionUID = -318638597959096228L;

	private static final String AGRUPAR_CARACTERISTICA_EXAME = "exames-agruparCaracteristicaExame";

	private static final String MANTER_GRUPO_CARACTERISTICA = "exames-manterGrupoCaracteristica";

	private static final String MANTER_CARACTERISTICA_RESULTADO = "exames-manterCaracteristicaResultado";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelExameGrupoCaracteristicaVO exameGrupoCaracteristicaVO = new AelExameGrupoCaracteristicaVO();
	
	private AelExameGrupoCaracteristicaVO exameGrupoCaracteristicaCopiaVO = null;
	
	private Integer seqParam;
	private String voltarPara; // O padrão é voltar para interface de pesquisa

	//suggestion exame material analise
	private VAelExameMatAnalise exameMaterialAnalise;
	
	//suggestion grupo
	private AelGrupoResultadoCaracteristica grupo;
	
	//suggestion caracteristica
	private AelResultadoCaracteristica caracteristica;
	
	//suggestion grupo cadastro
	private AelGrupoResultadoCaracteristica grupoCadastro;
	
	//suggestion caracteristica cadastro
	private AelResultadoCaracteristica caracteristicaCadastro; 
	
	private AelExameGrupoCaracteristicaVO itemExcluir;
	
	private AelExameGrupoCaracteristica exameGrupoCaracteristica = new AelExameGrupoCaracteristica();

	@Inject @Paginator
	private DynamicDataModel<AelExameGrupoCaracteristicaVO> dataModel;
	
	private AelExameGrupoCaracteristicaVO selecionado;
	
	private Boolean exibirBotaoImprime = Boolean.FALSE;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String pesquisar() {
		
		// Valida obrigatoriedade do campo exameMaterialAnalise
		if(exameGrupoCaracteristica.getExameMaterialAnalise() == null){
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Exames Material Análise");
			return null;
		}
	
		dataModel.reiniciarPaginator();
		if(recuperarCount()!=null && recuperarCount() >0){
		  exibirBotaoImprime = Boolean.TRUE;
		}else {
			exibirBotaoImprime = Boolean.FALSE;
		}
		
		return AGRUPAR_CARACTERISTICA_EXAME;
	}
	
	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.pesquisarExameGrupoCaracteristicaCount(exameGrupoCaracteristica);
	}

	@Override
	public List<AelExameGrupoCaracteristicaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.pesquisarExameGrupoCarateristica(exameGrupoCaracteristica, firstResult, maxResult, orderProperty, asc);
	}
	
	public void atribuirExameMaterialAnalise() {
		final AelExamesMaterialAnalise exameMaterialAnalise =  examesFacade.obterAelExamesMaterialAnalisePorId(this.exameMaterialAnalise.getId());
		exameGrupoCaracteristica.setExameMaterialAnalise(exameMaterialAnalise);
	}
	
	public String removerExameMaterialAnalise() {
		exameGrupoCaracteristica.setExameMaterialAnalise(null);
		this.limpar();
		return AGRUPAR_CARACTERISTICA_EXAME;
	}
	
	public void atribuirGrupo() {
		exameGrupoCaracteristica.setGrupoResultadoCaracteristica(this.grupo);
	}
	
	public void removerGrupo() {
		exameGrupoCaracteristica.setGrupoResultadoCaracteristica(null);
	}
	
	public void atribuirCaracteristica() {
		exameGrupoCaracteristica.setResultadoCaracteristica(this.caracteristica);
	}
	
	public void removerCaracteristica() {
		exameGrupoCaracteristica.setResultadoCaracteristica(null);
	}
	

	/**
	 * Método que insere ou atualiza um registro na tabela AghMedicoExternos <br>
	 * a partir da acao de gravar da tela de Cadastro de Medico Externo.
	 */
	public void gravar() {
		Boolean isSave = Boolean.FALSE;
				
		try {
			isSave = this.exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().getId() == null;
			
			
			if(isSave) {
				
				exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().setExameMaterialAnalise(
						 examesFacade.obterAelExamesMaterialAnalisePorId(exameMaterialAnalise.getId()));
				exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().setGrupoResultadoCaracteristica(grupoCadastro);
				exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().setResultadoCaracteristica(caracteristicaCadastro);
				
				cadastrosApoioExamesFacade.persistirAelExameGrupoCaracteristica(exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica());
				apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_SALVAR_AGRUPAR_CARACTERISTICA_EXAME", 
						exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().getCodigoFalante());
				
			} else { //edicao
				if(exameGrupoCaracteristicaCopiaVO != null) {
					exameGrupoCaracteristicaCopiaVO = null;
				}
				exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().setGrupoResultadoCaracteristica(grupoCadastro);
				exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().setResultadoCaracteristica(caracteristicaCadastro);
				cadastrosApoioExamesFacade.atualizarAelExameGrupoCaracteristica(exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica());
				apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_ATUALIZAR_AGRUPAR_CARACTERISTICA_EXAME",
						exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().getCodigoFalante());
			}

			cancelar();
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch(PersistenceException e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.INFO, "MSG_PERSISTIR_AGRUPAR_CARACTERISTICA_EXAME_ERRO");			
		}
	}

	public void cancelar() {
		if (exameGrupoCaracteristicaCopiaVO != null) {
			this.exameGrupoCaracteristicaCopiaVO.setEmEdicao(Boolean.FALSE);
			this.exameGrupoCaracteristicaCopiaVO = null;
		}
		
		//limpando as suggestions de cadastro
		this.grupoCadastro = null;
		this.caracteristicaCadastro = null;
		
		this.exameGrupoCaracteristicaVO.setAelExameGrupoCaracteristica(null);
		this.exameGrupoCaracteristicaVO = new AelExameGrupoCaracteristicaVO();
		this.exameGrupoCaracteristicaVO.getAelExameGrupoCaracteristica().setIndSituacao(DominioSituacao.A);
	}

	public String editar(AelExameGrupoCaracteristicaVO item) {
		if(this.exameGrupoCaracteristicaCopiaVO != null) {
			this.exameGrupoCaracteristicaCopiaVO.setEmEdicao(Boolean.FALSE);
			this.exameGrupoCaracteristicaCopiaVO = null;
		}
		
		AelExameGrupoCaracteristicaVO clone = new AelExameGrupoCaracteristicaVO(item);
		
		//setando as suggestions
		this.grupoCadastro = clone.getAelExameGrupoCaracteristica().getGrupoResultadoCaracteristica();
		this.caracteristicaCadastro = clone.getAelExameGrupoCaracteristica().getResultadoCaracteristica();
		
		clone.setEmEdicao(Boolean.TRUE);
		item.setEmEdicao(Boolean.TRUE);
		
		this.exameGrupoCaracteristicaCopiaVO = item;

		this.exameGrupoCaracteristicaVO = clone;
		
		return AGRUPAR_CARACTERISTICA_EXAME;
	}
	

	public Long obterMaterialAnaliseCount(String param) {
		return this.examesFacade.listarVExamesMaterialAnaliseCount(param);
	}
 
	public List<VAelExameMatAnalise> sbObterMaterialAnalise(String objPesquisa) {
		return this.returnSGWithCount(this.examesFacade.listarVExamesMaterialAnalise(objPesquisa),obterMaterialAnaliseCount(objPesquisa));
	}
	 
	public List<AelGrupoResultadoCaracteristica> sbObterGrupo(String objPesquisa) {
		return this.examesFacade.pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(objPesquisa);
	}

	public List<AelResultadoCaracteristica> sbObterCaracteristica(String objPesquisa) {
		try {
			return cadastrosApoioExamesFacade.listarResultadoCaracteristica((String) objPesquisa, null);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return new LinkedList<AelResultadoCaracteristica>();
		}
	}
	
	
	public void limpar() {
		dataModel.limparPesquisa();
		exameMaterialAnalise = null;
		grupo = null;
		caracteristica = null;
		cancelar();
		setExibirBotaoImprime(Boolean.FALSE);
		setExameGrupoCaracteristica(new AelExameGrupoCaracteristica());
	}
	
	public void excluir()  {
		try {
			cadastrosApoioExamesFacade.removerAelExameGrupoCaracteristica(selecionado.getAelExameGrupoCaracteristica().getId());
			apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_REMOVER_EXAME_GRUPO_CARACTERISTICA", selecionado.getAelExameGrupoCaracteristica().getCodigoFalante());
			
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Integrar com a estoria #5374
	 */
	public String chamarTelaGrupoCaracteristica() {
		return MANTER_GRUPO_CARACTERISTICA;
	}
	
	/**
	 * Integrar com a estoria #5370
	 */
	public String chamarTelCaracteristicaResultado() {
		return MANTER_CARACTERISTICA_RESULTADO;
	}
	
	/**
	 * Integrar com a estoria #5376
	 *
	public void chamarTelaImprimirListaCaracteristicaResultadoExame() {
		
	} */
	

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getSeqParam() {
		return seqParam;
	}

	public void setSeqParam(Integer seqParam) {
		this.seqParam = seqParam;
	}

	public VAelExameMatAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public AelGrupoResultadoCaracteristica getGrupo() {
		return grupo;
	}

	public AelResultadoCaracteristica getCaracteristica() {
		return caracteristica;
	}

	public void setExameMaterialAnalise(VAelExameMatAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public void setGrupo(AelGrupoResultadoCaracteristica grupo) {
		this.grupo = grupo;
	}

	public void setCaracteristica(AelResultadoCaracteristica caracteristica) {
		this.caracteristica = caracteristica;
	}

	public AelExameGrupoCaracteristicaVO getExameGrupoCaracteristicaVO() {
		return exameGrupoCaracteristicaVO;
	}

	public void setExameGrupoCaracteristicaVO(
			AelExameGrupoCaracteristicaVO exameGrupoCaracteristicaVO) {
		this.exameGrupoCaracteristicaVO = exameGrupoCaracteristicaVO;
	}

	public AelGrupoResultadoCaracteristica getGrupoCadastro() {
		return grupoCadastro;
	}

	public AelResultadoCaracteristica getCaracteristicaCadastro() {
		return caracteristicaCadastro;
	}

	public void setGrupoCadastro(AelGrupoResultadoCaracteristica grupoCadastro) {
		this.grupoCadastro = grupoCadastro;
	}

	public void setCaracteristicaCadastro(
			AelResultadoCaracteristica caracteristicaCadastro) {
		this.caracteristicaCadastro = caracteristicaCadastro;
	}

	public AelExameGrupoCaracteristicaVO getItemExcluir() {
		return itemExcluir;
	}

	public void setItemExcluir(AelExameGrupoCaracteristicaVO itemExcluir) {
		this.itemExcluir = itemExcluir;
	}

	public AelExameGrupoCaracteristica getExameGrupoCaracteristica() {
		return exameGrupoCaracteristica;
	}

	public void setExameGrupoCaracteristica(
			AelExameGrupoCaracteristica exameGrupoCaracteristica) {
		this.exameGrupoCaracteristica = exameGrupoCaracteristica;
	}


	public DynamicDataModel<AelExameGrupoCaracteristicaVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(
			DynamicDataModel<AelExameGrupoCaracteristicaVO> dataModel) {
		this.dataModel = dataModel;
	}


	public AelExameGrupoCaracteristicaVO getSelecionado() {
		return selecionado;
	}


	public void setSelecionado(AelExameGrupoCaracteristicaVO selecionado) {
		this.selecionado = selecionado;
	}


	public Boolean getExibirBotaoImprime() {
		return exibirBotaoImprime;
	}


	public void setExibirBotaoImprime(Boolean exibirBotaoImprime) {
		this.exibirBotaoImprime = exibirBotaoImprime;
	}

}