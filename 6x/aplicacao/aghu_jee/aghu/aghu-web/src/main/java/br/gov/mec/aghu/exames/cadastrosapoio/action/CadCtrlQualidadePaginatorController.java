package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class CadCtrlQualidadePaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 1261995388391265335L;

	private static final String CADASTRO_CONTROLE_QUALIDADE = "cadastroControleQualidade";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private Short convenioId;
	private Byte planoId;
	
	
	private AelCadCtrlQualidades filtros = new AelCadCtrlQualidades();

	@Inject @Paginator
	private DynamicDataModel<AelCadCtrlQualidades> dataModel;
	
	private AelCadCtrlQualidades selecionado;
	
	public enum CadCtrlQualidadePaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_MATERIAL_CONTROLE_QUALIDADE_POSSUI_ATENDIMENTO_DIVERSO;
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void excluir() {

		try {
			try {
				this.cadastrosApoioExamesFacade.excluirAelCadCtrlQualidades(selecionado.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_AEL_CAD_CTRL_QA", selecionado.getMaterial());
				
			} catch (PersistenceException pe) {
				if (pe.getCause() != null && pe.getCause() instanceof ConstraintViolationException) {
					final ConstraintViolationException cve = (ConstraintViolationException) pe.getCause();
					if (StringUtils.containsIgnoreCase(cve.getConstraintName(), "AEL_ATV_CCQ_FK1")) {
						throw new ApplicationBusinessException(
								CadCtrlQualidadePaginatorControllerExceptionCode.MENSAGEM_ERRO_MATERIAL_CONTROLE_QUALIDADE_POSSUI_ATENDIMENTO_DIVERSO,
								cve.getConstraintName());
					}
				} else {
					throw pe;
				}
			}
		} catch (BaseListException ex) {
			apresentarExcecaoNegocio(ex);
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return CADASTRO_CONTROLE_QUALIDADE;
	}
	
	public String editar(){
		return CADASTRO_CONTROLE_QUALIDADE;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		filtros = new AelCadCtrlQualidades();
		this.convenioId = null;
		this.planoId = null;
	}

	@Override
	public List<AelCadCtrlQualidades> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.obterCadCtrlQualidadesList(filtros, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.obterCadCtrlQualidadesListCount(filtros);
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}
	
	public void atribuirPlano() {
		if (filtros != null && filtros.getConvenioSaudePlano() != null) {
			this.convenioId = filtros.getConvenioSaudePlano().getId().getCnvCodigo();
			this.planoId = filtros.getConvenioSaudePlano().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			filtros.setConvenioSaudePlano(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));		
		}
	}

	public AelCadCtrlQualidades getFiltros() {
		return filtros;
	}

	public void setFiltros(AelCadCtrlQualidades filtros) {
		this.filtros = filtros;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public DynamicDataModel<AelCadCtrlQualidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelCadCtrlQualidades> dataModel) {
		this.dataModel = dataModel;
	}

	public AelCadCtrlQualidades getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelCadCtrlQualidades selecionado) {
		this.selecionado = selecionado;
	}
}