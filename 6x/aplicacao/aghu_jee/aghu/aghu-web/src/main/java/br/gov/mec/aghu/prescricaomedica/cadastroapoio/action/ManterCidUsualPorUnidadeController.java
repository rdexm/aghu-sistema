package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCidUnidFuncional;
import br.gov.mec.aghu.model.MpmCidUnidFuncionalId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCidUsualPorUnidadeController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1398178924220537874L;

	private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private AghCid aghCid;
	//private Integer pesquisaCid = null;
	private MpmCidUnidFuncional mpmCidUnidFuncional;
	private MpmCidUnidFuncionalId id;
	private MpmCidUnidFuncional cidUnidadeFuncionalExcluir;

	@Inject @Paginator
	private DynamicDataModel<MpmCidUnidFuncional> dataModel;
	private MpmCidUnidFuncional parametroSelecionado;
	
	@Inject
	@SelectionQualifier
	private Instance<PesquisaCidVO> pesquisaCidRetorno;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	// suggestion - unidade funcional
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String param) {
		ConstanteAghCaractUnidFuncionais[] prm = null;
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
				param, DominioSituacao.A, Boolean.TRUE, Boolean.TRUE,
				Boolean.TRUE, Arrays.asList(AghUnidadesFuncionais.Fields.DESCRICAO), prm),pesquisarUnidadeFuncionalCount(param));
	}

	public Long pesquisarUnidadeFuncionalCount(String param) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(param, DominioSituacao.A, Boolean.TRUE, Boolean.TRUE, null);
	}

	// suggestion - cid
	public List<AghCid> pesquisarCid(String param) {
		return this.returnSGWithCount(this.aghuFacade.obterCidPorNomeCodigoAtivaPaginado((String) param),pesquisarCidCount(param));
	}

	public Long pesquisarCidCount(String param) {
		return this.aghuFacade.obterCidPorNomeCodigoAtivaCount((String) param);
	}
	
	public void iniciar() {
	 

		
		if(this.pesquisaCidRetorno != null){
			PesquisaCidVO pesquisaCidVO = pesquisaCidRetorno.get();
			this.aghCid = pesquisaCidVO.getCid();
		}
		
		this.mpmCidUnidFuncional = new MpmCidUnidFuncional();
		this.mpmCidUnidFuncional.setId(new MpmCidUnidFuncionalId());
		this.id = new MpmCidUnidFuncionalId();
	
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		this.aghUnidadesFuncionais = null;
		this.aghCid = null;
		iniciar();
	}

	public void excluir() {
		this.prescricaoMedicaFacade.excluirMpmCidAtendimento(this.parametroSelecionado.getId());
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_CID_POR_UNIDADE_FUNCIONAL");
		cidUnidadeFuncionalExcluir = new MpmCidUnidFuncional();
		this.iniciar();
		this.aghCid = null;
	}

	public void gravar() {
		this.mpmCidUnidFuncional.getId().setUnfSeq(this.aghUnidadesFuncionais.getSeq());
		this.mpmCidUnidFuncional.getId().setCidSeq(this.aghCid.getSeq());
		this.mpmCidUnidFuncional.setCid(this.aghCid);
		this.mpmCidUnidFuncional.setUnidadeFuncional(this.aghUnidadesFuncionais);
		try {
			this.prescricaoMedicaFacade.persistirCidUnidadeFuncional(this.mpmCidUnidFuncional);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_CID_POR_UNIDADE_FUNCIONAL");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			return;
		}

		this.aghCid = null;
	}

	public String pesquisaCidCapitulo() {
		return PAGE_PESQUISA_CID;
	}

	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade.listaCidUnidadeFuncionalCount(this.aghUnidadesFuncionais);
	}

	@Override
	public List<MpmCidUnidFuncional> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.prescricaoMedicaFacade.listaCidUnidadeFuncional(firstResult, maxResult, orderProperty, asc, this.aghUnidadesFuncionais);
	}

	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	public MpmCidUnidFuncional getMpmCidUnidFuncional() {
		return mpmCidUnidFuncional;
	}

	public void setMpmCidUnidFuncional(MpmCidUnidFuncional mpmCidUnidFuncional) {
		this.mpmCidUnidFuncional = mpmCidUnidFuncional;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public AghCid getAghCid() {
		return aghCid;
	}

	public MpmCidUnidFuncional getCidUnidadeFuncionalExcluir() {
		return cidUnidadeFuncionalExcluir;
	}

	public void setCidUnidadeFuncionalExcluir(MpmCidUnidFuncional cidUnidadeFuncionalExcluir) {
		this.cidUnidadeFuncionalExcluir = cidUnidadeFuncionalExcluir;
	}

	public MpmCidUnidFuncionalId getId() {
		return id;
	}

	public void setId(MpmCidUnidFuncionalId id) {
		this.id = id;
	}

	public DynamicDataModel<MpmCidUnidFuncional> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmCidUnidFuncional> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmCidUnidFuncional getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmCidUnidFuncional parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}
