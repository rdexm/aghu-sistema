package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class DiagnosticosEtiologiasCRUDController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<EpeFatRelDiagnostico> dataModel;

	private static final long serialVersionUID = 126974068299977703L;
	private static final String PAGE_DIAGNOSTICOS_CUIDADOS_CRUD = "diagnosticosCuidadosCRUD";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private Short snbGnbSeq;
	private Short snbSequencia;
	private Short sequencia;
	private Short freSeq;
	private String indSituacao;
	private String indSituacaoDiagEti;
	private Boolean diagnosticoSituacao;
	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private EpeDiagnostico diagnostico;
	private EpeFatRelacionado etiologia;
	private Boolean suggestionAdicionar;
	private Boolean ativaBotoesAcao;
	private Boolean situacao;
	private String cameFrom;
	private EpeFatRelDiagnostico etiologiaDiagnostico;
	private EpeFatRelDiagnostico etiologiaDiagnosticoSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		EpeDiagnosticoId diagnosticoId = new EpeDiagnosticoId();
		diagnosticoId.setSnbGnbSeq(snbGnbSeq);
		diagnosticoId.setSnbSequencia(snbSequencia);
		diagnostico = new EpeDiagnostico();
		diagnostico.setId(diagnosticoId);
		if (getSuggestionAdicionar() == null) {
			setSuggestionAdicionar(true);
		}

		if (snbGnbSeq != null && snbSequencia != null && sequencia != null) {
			EpeDiagnosticoId id = new EpeDiagnosticoId();
			id.setSnbGnbSeq(snbGnbSeq);
			id.setSnbSequencia(snbSequencia);
			id.setSequencia(sequencia);
			diagnostico = prescricaoEnfermagemFacade.obterDiagnosticoPorChavePrimaria(id);
			if (DominioSituacao.A.toString().equals(indSituacao)) {
				diagnosticoSituacao = true;
				setAtivaBotoesAcao(true);
			} else {
				diagnosticoSituacao = false;
				setAtivaBotoesAcao(false);
			}
			grupo = this.prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(snbGnbSeq);
			EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId();
			idSubgrupo.setGnbSeq(snbGnbSeq);
			idSubgrupo.setSequencia(snbSequencia);
			subGrupo = this.prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(idSubgrupo);

		}
		setSituacao(true);

		if (!dataModel.getPesquisaAtiva()) {
			dataModel.reiniciarPaginator();
		}

	
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemFacade.pesquisarEtiologiasDiagnosticosCount(snbGnbSeq, snbSequencia, sequencia);
	}

	@Override
	public List<EpeFatRelDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return prescricaoEnfermagemFacade.pesquisarEtiologiasDiagnosticos(snbGnbSeq, snbSequencia, sequencia, firstResult,
				maxResult, orderProperty, asc);
	}

	public void ativarBotaoAdicionar() {
		setSuggestionAdicionar(false);
	}

	public List<EpeFatRelacionado> pesquisarEtiologias(String filtro) {
		String parametro = filtro != null ? (String) filtro : null;
		return prescricaoEnfermagemApoioFacade.pesquisarEtiologiasNaoRelacionadas(parametro, snbGnbSeq, snbSequencia, sequencia);
	}

	public Long pesquisarEtiologiasCount(Object filtro) {
		String parametro = filtro != null ? (String) filtro : null;
		return prescricaoEnfermagemApoioFacade.pesquisarEtiologiasNaoRelacionadasCount(parametro, snbGnbSeq, snbSequencia,
				sequencia);
	}

	public void adicionar() {
		try {
			EpeFatRelDiagnostico etiologiaDiagnostico = new EpeFatRelDiagnostico();
			EpeFatRelDiagnosticoId etiologiaDiagnosticoId = new EpeFatRelDiagnosticoId();

			etiologiaDiagnosticoId.setFreSeq(etiologia.getSeq());
			etiologiaDiagnosticoId.setDgnSequencia(sequencia);
			etiologiaDiagnosticoId.setDgnSnbGnbSeq(snbGnbSeq);
			etiologiaDiagnosticoId.setDgnSnbSequencia(snbSequencia);
			etiologiaDiagnosticoId.setFreSeq(etiologia.getSeq());
			etiologiaDiagnostico.setId(etiologiaDiagnosticoId);
			etiologiaDiagnostico.setSituacao((situacao) ? DominioSituacao.A : DominioSituacao.I);
			etiologiaDiagnostico.setFatRelacionado(etiologia);

			prescricaoEnfermagemApoioFacade.persistirEtiologiaDiagnostico(etiologiaDiagnostico);
			limparPesquisa();

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICAO_MANTER_DIAGNOSTICO_ETIOLOGIA", etiologiaDiagnostico
					.getFatRelacionado().getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}

	public void selecionarEtiologia(EpeFatRelDiagnostico etiologiaDiagnostico) {
		this.etiologiaDiagnostico = etiologiaDiagnostico;
	}

	public void excluir() {
		try {
			prescricaoEnfermagemApoioFacade.excluirEtiologiaDiagnostico(etiologiaDiagnosticoSelecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MANTER_DIAGNOSTICO_ETIOLOGIA",
					etiologiaDiagnosticoSelecionado.getFatRelacionado().getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}

	public String cancelar() {
		
		limparPesquisa();
		return cameFrom;
	}

	public void ativarDesativar(EpeFatRelDiagnostico epeEtiologiaDiagnostico) {
		
		this.etiologiaDiagnostico = epeEtiologiaDiagnostico;
		DominioSituacao situacaoAnterior = etiologiaDiagnostico.getSituacao();
	
		try {
			
			etiologiaDiagnostico.setSituacao(DominioSituacao.A.equals(etiologiaDiagnostico.getSituacao()) ? DominioSituacao.I
					: DominioSituacao.A);
			prescricaoEnfermagemApoioFacade.ativarDesativarEtiologiaDiagnostico(etiologiaDiagnostico);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MANTER_DIAGNOSTICO_ETIOLOGIA",
					etiologiaDiagnostico.getFatRelacionado().getDescricao());
		} catch (ApplicationBusinessException e) {
			etiologiaDiagnostico.setSituacao(situacaoAnterior);
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		etiologia = null;
		situacao = Boolean.TRUE;
		suggestionAdicionar = Boolean.TRUE;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public String redirecionarDiagnosticosCuidados() {
		return PAGE_DIAGNOSTICOS_CUIDADOS_CRUD;
	}

	// Gets and Sets

	public Short getSnbGnbSeq() {
		return snbGnbSeq;
	}

	public void setSnbGnbSeq(Short snbGnbSeq) {
		this.snbGnbSeq = snbGnbSeq;
	}

	public Short getSnbSequencia() {
		return snbSequencia;
	}

	public void setSnbSequencia(Short snbSequencia) {
		this.snbSequencia = snbSequencia;
	}

	public Short getSequencia() {
		return sequencia;
	}

	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}

	public EpeDiagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(EpeDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	public void setGrupo(EpeGrupoNecesBasica grupo) {
		this.grupo = grupo;
	}

	public EpeGrupoNecesBasica getGrupo() {
		return grupo;
	}

	public void setSubGrupo(EpeSubgrupoNecesBasica subGrupo) {
		this.subGrupo = subGrupo;
	}

	public EpeSubgrupoNecesBasica getSubGrupo() {
		return subGrupo;
	}

	public void setDiagnosticoSituacao(Boolean diagnosticoSituacao) {
		this.diagnosticoSituacao = diagnosticoSituacao;
	}

	public Boolean getDiagnosticoSituacao() {
		return diagnosticoSituacao;
	}

	public EpeFatRelacionado getEtiologia() {
		return etiologia;
	}

	public void setEtiologia(EpeFatRelacionado etiologia) {
		this.etiologia = etiologia;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getSuggestionAdicionar() {
		return suggestionAdicionar;
	}

	public void setSuggestionAdicionar(Boolean suggestionAdicionar) {
		this.suggestionAdicionar = suggestionAdicionar;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public boolean isAtivo(EpeFatRelDiagnostico etiologiaDiagnostico) {
		return DominioSituacao.A.equals(etiologiaDiagnostico.getSituacao());
	}

	public void setEtiologiaDiagnostico(EpeFatRelDiagnostico etiologiaDiagnostico) {
		this.etiologiaDiagnostico = etiologiaDiagnostico;
	}

	public EpeFatRelDiagnostico getEtiologiaDiagnostico() {
		return etiologiaDiagnostico;
	}

	public void setAtivaBotoesAcao(Boolean ativaBotoesAcao) {
		this.ativaBotoesAcao = ativaBotoesAcao;
	}

	public Boolean getAtivaBotoesAcao() {
		return ativaBotoesAcao;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setFreSeq(Short freSeq) {
		this.freSeq = freSeq;
	}

	public Short getFreSeq() {
		return freSeq;
	}

	public void setIndSituacaoDiagEti(String indSituacaoDiagEti) {
		this.indSituacaoDiagEti = indSituacaoDiagEti;
	}

	public String getIndSituacaoDiagEti() {
		return indSituacaoDiagEti;
	}

	public DynamicDataModel<EpeFatRelDiagnostico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeFatRelDiagnostico> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeFatRelDiagnostico getEtiologiaDiagnosticoSelecionado() {
		return etiologiaDiagnosticoSelecionado;
	}

	public void setEtiologiaDiagnosticoSelecionado(EpeFatRelDiagnostico etiologiaDiagnosticoSelecionado) {
		this.etiologiaDiagnosticoSelecionado = etiologiaDiagnosticoSelecionado;
	}
}
