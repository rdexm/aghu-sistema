package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeCaractDefDiagnosticoId;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class DiagnosticosSinaisSintomasCRUDController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<EpeCaractDefDiagnostico> dataModel;

	private static final long serialVersionUID = 126974068299977703L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private Short snbGnbSeq;
	private Short snbSequencia;
	private Short sequencia;
	private Short freSeq;
	private DominioSituacao indSituacao;
	private Boolean diagnosticoSituacao;
	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private EpeDiagnostico diagnostico;
	private EpeCaractDefinidora sinalSintoma;
	private Boolean suggestionAdiconar;
	private Boolean situacao;
	private EpeCaractDefDiagnostico sinalSintomaDiagnostico;
	private EpeCaractDefDiagnostico diagnosticosSinaisSintomasSelection;
	private String origem;

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
			if (DominioSituacao.A.equals(indSituacao)) {
				diagnosticoSituacao = true;
			} else {
				diagnosticoSituacao = false;
			}
			grupo = prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(snbGnbSeq);
			EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId();
			idSubgrupo.setGnbSeq(snbGnbSeq);
			idSubgrupo.setSequencia(snbSequencia);
			subGrupo = prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(idSubgrupo);
		}
		setSituacao(true);
		if (!dataModel.getPesquisaAtiva()) {
			dataModel.reiniciarPaginator();
		}
	
	}

	public void adicionar() {
		EpeCaractDefDiagnostico sinalSintomaDiagnostico = new EpeCaractDefDiagnostico();
		EpeCaractDefDiagnosticoId sinalSintomaDiagnosticoId = new EpeCaractDefDiagnosticoId();
		sinalSintomaDiagnosticoId.setCdeCodigo(sinalSintoma.getCodigo());
		sinalSintomaDiagnosticoId.setDgnSequencia(sequencia);
		sinalSintomaDiagnosticoId.setDgnSnbGnbSeq(snbGnbSeq);
		sinalSintomaDiagnosticoId.setDgnSnbSequencia(snbSequencia);
		sinalSintomaDiagnostico.setId(sinalSintomaDiagnosticoId);
		sinalSintomaDiagnostico.setCaractDefinidora(sinalSintoma);

		// TODO VER COM CRISTIANO
		// prescricaoEnfermagemApoioFacade.epetCrdBri(sinalSintomaDiagnostico);
		prescricaoEnfermagemApoioFacade.inserirEpeCaractDefDiagnostico(sinalSintomaDiagnostico);
		limparPesquisa();
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICAO_MANTER_DIAGNOSTICO_SINAL_SINTOMA", sinalSintomaDiagnostico
				.getCaractDefinidora().getDescricao());
		dataModel.reiniciarPaginator();

	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemApoioFacade.pesquisarCaractDefDiagnosticoPorSubgrupoCount(snbGnbSeq, snbSequencia, sequencia);
	}

	@Override
	public List<EpeCaractDefDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return prescricaoEnfermagemApoioFacade.pesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia,
				firstResult, maxResult, orderProperty, asc);
	}

	public void excluir() {
		prescricaoEnfermagemApoioFacade.excluirEpeCaractDefDiagnostico(diagnosticosSinaisSintomasSelection.getId());
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MANTER_DIAGNOSTICO_SINAL_SINTOMA",
				diagnosticosSinaisSintomasSelection.getCaractDefinidora().getDescricao());
		dataModel.reiniciarPaginator();
	}

	public String cancelar() {
		limparPesquisa();
		return origem;
	}

	public List<EpeCaractDefinidora> pesquisarSinaisSintomas(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemApoioFacade.pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico((String) filtro,
				snbGnbSeq, snbSequencia, sequencia),pesquisarSinaisSintomasCount(filtro));
	}

	public Long pesquisarSinaisSintomasCount(String filtro) {
		return prescricaoEnfermagemApoioFacade.pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnosticoCount((String) filtro,
				snbGnbSeq, snbSequencia, sequencia);
	}

	public void ativarBotaoAdicionar() {
		setSuggestionAdiconar(false);
	}

	public void limparPesquisa() {
		sinalSintoma = null;
		sinalSintoma = null;
		situacao = Boolean.TRUE;
		suggestionAdiconar = Boolean.TRUE;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
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

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
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

	public EpeCaractDefDiagnostico getSinalSintomaDiagnostico() {
		return sinalSintomaDiagnostico;
	}

	public void setSinalSintomaDiagnostico(EpeCaractDefDiagnostico sinalSintomaDiagnostico) {
		this.sinalSintomaDiagnostico = sinalSintomaDiagnostico;
	}

	public EpeCaractDefinidora getSinalSintoma() {
		return sinalSintoma;
	}

	public void setSinalSintoma(EpeCaractDefinidora sinalSintoma) {
		this.sinalSintoma = sinalSintoma;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public DynamicDataModel<EpeCaractDefDiagnostico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeCaractDefDiagnostico> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeCaractDefDiagnostico getDiagnosticosSinaisSintomasSelection() {
		return diagnosticosSinaisSintomasSelection;
	}

	public void setDiagnosticosSinaisSintomasSelection(EpeCaractDefDiagnostico diagnosticosSinaisSintomasSelection) {
		this.diagnosticosSinaisSintomasSelection = diagnosticosSinaisSintomasSelection;
	}
}
