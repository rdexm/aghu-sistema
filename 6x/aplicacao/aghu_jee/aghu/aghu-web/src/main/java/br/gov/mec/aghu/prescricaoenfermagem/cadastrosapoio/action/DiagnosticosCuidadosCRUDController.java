package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoDiagnosticoId;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class DiagnosticosCuidadosCRUDController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<EpeCuidadoDiagnostico> dataModel;

	private static final long serialVersionUID = 126974068299977703L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private Short snbGnbSeq;
	private Short snbSequencia;
	private Short sequencia;
	private Short freSeq;
	private String indSituacao;
	private Boolean diagnosticoSituacao;
	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private EpeDiagnostico diagnostico;
	private EpeFatRelacionado etiologia;
	private EpeCuidados cuidado;
	private Boolean suggestionAdiconar;
	private Boolean situacao;
	private String cameFrom;
	private EpeCuidadoDiagnostico cuidadoDiagnostico;

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
		if (getSuggestionAdiconar() == null) {
			setSuggestionAdiconar(true);
		}

		if (snbGnbSeq != null && snbSequencia != null && sequencia != null) {
			EpeDiagnosticoId id = new EpeDiagnosticoId();
			id.setSnbGnbSeq(snbGnbSeq);
			id.setSnbSequencia(snbSequencia);
			id.setSequencia(sequencia);
			diagnostico = prescricaoEnfermagemFacade.obterDiagnosticoPorChavePrimaria(id);
			if (DominioSituacao.A.getDescricao().equals(indSituacao)) {
				diagnosticoSituacao = true;
			} else {
				diagnosticoSituacao = false;
			}
			grupo = this.prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(snbGnbSeq);
			EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId();
			idSubgrupo.setGnbSeq(snbGnbSeq);
			idSubgrupo.setSequencia(snbSequencia);
			subGrupo = this.prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(idSubgrupo);
			etiologia = prescricaoEnfermagemApoioFacade.obterEpeFatRelacionadoPorChavePrimaria(freSeq);
		}
		setSituacao(true);

		if (!dataModel.getPesquisaAtiva()) {
			dataModel.reiniciarPaginator();
		}

	
	}

	public void adicionar() {
		try {
			EpeCuidadoDiagnostico cuidadoDiagnostico = new EpeCuidadoDiagnostico();
			EpeCuidadoDiagnosticoId cuidadoDiagnosticoId = new EpeCuidadoDiagnosticoId();

			cuidadoDiagnosticoId.setCuiSeq(cuidado.getSeq());
			cuidadoDiagnosticoId.setFdgDgnSequencia(sequencia);
			cuidadoDiagnosticoId.setFdgDgnSnbGnbSeq(snbGnbSeq);
			cuidadoDiagnosticoId.setFdgDgnSnbSequencia(snbSequencia);
			cuidadoDiagnosticoId.setFdgFreSeq(etiologia.getSeq());
			cuidadoDiagnostico.setId(cuidadoDiagnosticoId);
			cuidadoDiagnostico.setSituacao((situacao) ? DominioSituacao.A : DominioSituacao.I);
			cuidadoDiagnostico.setCuidado(cuidado);

			prescricaoEnfermagemApoioFacade.persistirCuidadoDiagnostico(cuidadoDiagnostico);
			limparPesquisa();

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICAO_MANTER_DIAGNOSTICO_CUIDADO", cuidadoDiagnostico
					.getCuidado().getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemFacade.pesquisarCuidadosDiagnosticosCount(snbGnbSeq, snbSequencia, sequencia, freSeq);
	}

	@Override
	public List<EpeCuidadoDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return prescricaoEnfermagemFacade.pesquisarCuidadosDiagnosticos(snbGnbSeq, snbSequencia, sequencia, freSeq, firstResult,
				maxResult, orderProperty, asc);
	}

	public void excluir() {
		try {
			prescricaoEnfermagemApoioFacade.excluirCuidadoDiagnostico(cuidadoDiagnostico.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MANTER_DIAGNOSTICO_CUIDADO", cuidadoDiagnostico
					.getCuidado().getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}

	public String cancelar() {
		limparPesquisa();
		return cameFrom;
	}

	public List<EpeCuidados> pesquisarCuidados(String filtro) {
		return prescricaoEnfermagemFacade.pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticos(filtro,
				snbGnbSeq, snbSequencia, sequencia, freSeq);
	}

	public Long pesquisarCuidadosCount(String filtro) {
		return prescricaoEnfermagemFacade.pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticosCount(filtro,
				snbGnbSeq, snbSequencia, sequencia, freSeq);
	}

	public void ativarBotaoAdicionar() {
		setSuggestionAdiconar(false);
	}

	public void limparPesquisa() {
		setCuidado(null);
		setSituacao(true);
		setSuggestionAdiconar(true);
		dataModel.setPesquisaAtiva(false);
		this.iniciar();
	}

	public void ativarDesativar(EpeCuidadoDiagnostico epeCuidadoDiagnostico) {
		try {
			epeCuidadoDiagnostico.setSituacao(DominioSituacao.A.equals(epeCuidadoDiagnostico.getSituacao()) ? DominioSituacao.I
					: DominioSituacao.A);
			prescricaoEnfermagemApoioFacade.atualizarCuidadoDiagnostico(epeCuidadoDiagnostico);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MANTER_DIAGNOSTICO_CUIDADO",
					epeCuidadoDiagnostico.getCuidado().getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}	

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

	public Short getFreSeq() {
		return freSeq;
	}

	public void setFreSeq(Short freSeq) {
		this.freSeq = freSeq;
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

	public EpeCuidados getCuidado() {
		return cuidado;
	}

	public void setCuidado(EpeCuidados cuidado) {
		this.cuidado = cuidado;
	}

	public Boolean getSuggestionAdiconar() {
		return suggestionAdiconar;
	}

	public void setSuggestionAdiconar(Boolean suggestionAdiconar) {
		this.suggestionAdiconar = suggestionAdiconar;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public EpeCuidadoDiagnostico getCuidadoDiagnostico() {
		return cuidadoDiagnostico;
	}

	public void setCuidadoDiagnostico(EpeCuidadoDiagnostico cuidadoDiagnostico) {
		this.cuidadoDiagnostico = cuidadoDiagnostico;
	}

	public boolean isAtivo(EpeCuidadoDiagnostico epeCuidadoDiagnostico) {
		return DominioSituacao.A.equals(epeCuidadoDiagnostico.getSituacao());
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public DynamicDataModel<EpeCuidadoDiagnostico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeCuidadoDiagnostico> dataModel) {
		this.dataModel = dataModel;
	}
}
