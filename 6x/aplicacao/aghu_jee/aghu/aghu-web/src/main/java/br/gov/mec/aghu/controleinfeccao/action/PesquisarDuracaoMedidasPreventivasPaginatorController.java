package br.gov.mec.aghu.controleinfeccao.action;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.MciDuracaoMedidaPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class PesquisarDuracaoMedidasPreventivasPaginatorController extends ActionController implements ActionPaginator {

	private static final String PAGINA_MANTER_DURACAO_MEDIDAS = "controleinfeccao-manterDuracaoMedidasPreventivas";
	private static final long serialVersionUID = -5159107032113993399L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	// formulario
	private String descricao;
	
	private DominioSituacao situacao;

	// lista
	@Inject @Paginator
	private DynamicDataModel<MciDuracaoMedidaPreventivasVO> dataModel;
	
	private MciDuracaoMedidaPreventivasVO selecionado = new MciDuracaoMedidaPreventivasVO();
	
	private List<MciDuracaoMedidaPreventiva> lista = new ArrayList<MciDuracaoMedidaPreventiva>();
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterDuracaoMedidasPreventivas", "manter");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}


	public void limpar() {	
		this.descricao = null;
		this.situacao = null;
		this.dataModel.setPesquisaAtiva(false);
		dataModel.limparPesquisa();
		lista = new ArrayList<MciDuracaoMedidaPreventiva>();
	}
	
	
	public String novo(){
		return PAGINA_MANTER_DURACAO_MEDIDAS;
	}
	
	public String editar(){
		return PAGINA_MANTER_DURACAO_MEDIDAS;
	}
	
	public void excluir()  {
		try {
			controleInfeccaoFacade.removerDuracaoMedidaPreventiva(selecionado.getSeq(), selecionado.getCriadoEm());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_DURACAO_MEDIDAS",selecionado.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	@Override
	public Long recuperarCount() {
		Long count = controleInfeccaoFacade.obterDuracaoMedidaPreventivaPorDescricaoSituacaoCount(this.descricao, this.situacao);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public  List<MciDuracaoMedidaPreventivasVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		lista = controleInfeccaoFacade.obterDuracaoMedidaPreventivaPorDescricaoSituacao(this.descricao, this.situacao, firstResult, maxResult, orderProperty, asc);
		return populaVO(lista);
	}
	

	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public DominioSituacao getSituacao() {
		return situacao;
	}


	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public MciDuracaoMedidaPreventivasVO getSelecionado() {
		return selecionado;
	}


	public void setSelecionado(MciDuracaoMedidaPreventivasVO selecionado) {
		this.selecionado = selecionado;
	}


	public DynamicDataModel<MciDuracaoMedidaPreventivasVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<MciDuracaoMedidaPreventivasVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<MciDuracaoMedidaPreventivasVO> populaVO(List<MciDuracaoMedidaPreventiva> lista){
		List<MciDuracaoMedidaPreventivasVO> listaVO = new ArrayList<MciDuracaoMedidaPreventivasVO>();
		for (MciDuracaoMedidaPreventiva linha : lista) {
			MciDuracaoMedidaPreventivasVO item = new MciDuracaoMedidaPreventivasVO();
			item.setSeq(linha.getSeq());
			item.setDescricao(linha.getDescricao());
			item.setCriadoEm(linha.getCriadoEm());
			item.setSituacao(linha.getSituacao());	
			if(linha.getAlteradoEm() != null){
				item.setCriadoAlterado(linha.getAlteradoEm());	
			}else if(linha.getCriadoEm() != null){
				item.setCriadoAlterado(linha.getCriadoEm());	
			}else{
				item.setCriadoAlterado(null);
			}
			listaVO.add(item);
		}
		return listaVO;
	}


	public List<MciDuracaoMedidaPreventiva> getLista() {
		return lista;
	}


	public void setLista(List<MciDuracaoMedidaPreventiva> lista) {
		this.lista = lista;
	}
	
	

//redirect edicao
//	public String editarMedicamento(Integer matcodigo){
//		this.matCodigoMedicamentoEdicao = matcodigo;		
//		return PAGINA_FARMACIA_MEDICAMENTO_CRUD;
//	}

	
// modal boolean
//	public void desabilitarExibicaoModal(){
//		this.exibirModal = false;
//	}

 // buscar parametro
// AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROATIVOS_INICIO_TRATAMENTO);

	
// data
//	protected Date populaDataHora(String hora) throws ApplicationBusinessException {
//		try {
//			if (StringUtils.isNotBlank(hora)) {
//				String[] arrayHora = hora.split(":");
//				if (arrayHora.length == 2) {
//					Calendar cal = Calendar.getInstance();
//					cal.set(Calendar.DAY_OF_MONTH, 1);
//					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(arrayHora[0]));
//					cal.set(Calendar.MINUTE, Integer.valueOf(arrayHora[1]));
//					cal.set(Calendar.SECOND, 0);
//					cal.set(Calendar.MILLISECOND, 0);
//
//					return cal.getTime();
//				}
//			}
//			return null;
//		} catch (Exception e) {
//			LOG.error(e.getMessage(),e);
//			throw new ApplicationBusinessException(
//					ManterPrescricaoMedicamentoExceptionCode.HORA_INVALIDA);
//		}
//	}

	// msg try catch
	// apresentarExcecaoNegocio(e);

	// msg
	// this.apresentarMsgNegocio(Severity.ERROR, "Medicamento " + medicamento.getDescricaoEditada() + " já adicionado a solução.");

	// msg throw 
	// throw new ApplicationBusinessException(ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE);

	// voltar
//	public String voltar() {
//		this.prescricaoMedicaVO = null;
//		this.limpar();
//		return PAGINA_MANTER_PRESCRICAO_MEDICA;
//	}

}
