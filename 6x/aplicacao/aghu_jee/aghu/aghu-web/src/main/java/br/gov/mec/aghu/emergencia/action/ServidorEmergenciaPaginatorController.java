package br.gov.mec.aghu.emergencia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorEmergenciaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller das ações da pagina de listagem de servidores da emergência.
 * 
 * @author luismoura
 * 
 */
public class ServidorEmergenciaPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 6589546988357451478L;

	private final String PAGE_CAD_SERV_EMERG = "servidorEmergenciaCRUD";

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private Short vinCodigo;
	private Integer matricula;
	private ServidorEmergenciaVO servidorEmergencia;
	private DominioSituacao indSituacao;
	private String nome;

	@Inject @Paginator
	private DynamicDataModel<ServidorEmergenciaVO> dataModel;

	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.vinCodigo = null;
		this.matricula = null;
		this.servidorEmergencia = null;
		this.indSituacao = null;
		this.nome = null;
	}

	/**
	 * Ação do botão PESQUISAR da pagina de listagem de servidores da emergência.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Ação do botão LIMPAR da pagina de listagem de servidores da emergência.
	 */
	public void limparPesquisa() {
		this.vinCodigo = null;
		this.matricula = null;
		this.nome = null;
		this.servidorEmergencia = null;
		this.indSituacao = null;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Ação do botão EXCLUIR da pagina de listagem de servidores da emergência.
	 */
	public void excluir() {
		try {
			if (servidorEmergencia != null && servidorEmergencia.getServidorEmergencia() != null) {
				this.emergenciaFacade.excluirMamEmgServidores(servidorEmergencia.getServidorEmergencia().getSeq());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ESPECIALIDADE_SERVIDOR");
			}
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão NOVO da pagina de listagem de servidores da emergência.
	 * 
	 * @return
	 */
	public String novo() {
		this.servidorEmergencia = null;
		return PAGE_CAD_SERV_EMERG;
	}

	/**
	 * Ação do botão EDITAR da pagina de listagem de servidores da emergência.
	 * 
	 * @return
	 */
	public String editar() {
		return PAGE_CAD_SERV_EMERG;
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		String situacao = indSituacao != null ? indSituacao.toString() : null;
		try {
			return this.emergenciaFacade.pesquisarServidorEmergenciaVOCount(vinCodigo, matricula, situacao, nome);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServidorEmergenciaVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		String situacao = indSituacao != null ? indSituacao.toString() : null;
		try {
			return this.emergenciaFacade.pesquisarServidorEmergenciaVO(firstResult, maxResults, orderProperty, asc, vinCodigo, matricula, situacao, nome);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public Boolean getBolIndSituacao(String indSituacao) {
		return DominioSituacao.A.toString().equals(indSituacao);
	}

	// ### GETs e SETs ###

	public DynamicDataModel<ServidorEmergenciaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ServidorEmergenciaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public ServidorEmergenciaVO getServidorEmergencia() {
		return servidorEmergencia;
	}

	public void setServidorEmergencia(ServidorEmergenciaVO servidorEmergencia) {
		this.servidorEmergencia = servidorEmergencia;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
