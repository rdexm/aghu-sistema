package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class DiagnosticoCRUDController extends ActionController {

	private static final long serialVersionUID = 126974068299977702L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private static final String RETORNAR_LIST = "diagnosticosList";
	private static final String REDIRECIONA_DIAGNOSTICOS_ETIOLOGIAS_CRUD = "diagnosticosEtiologiasCRUD";
	private static final String REDIRECIONA_DIAGNOSTICOS_SINAIS_SINTOMAS_CRUD = "diagnosticosSinaisSintomasCRUD";

	private Short snbGnbSeq;
	private Short snbSequencia;
	private Short sequencia;
	private String indSituacao;
	private Boolean diagnosticoSituacao;
	private EpeDiagnostico diagnostico;
	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private String cameFrom;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		EpeDiagnosticoId diagnosticoId = new EpeDiagnosticoId();
		diagnosticoId.setSnbGnbSeq(snbGnbSeq);
		diagnosticoId.setSnbSequencia(snbSequencia);
		
		sequencia = sequencia == 0 ? null : sequencia;
		diagnosticoId.setSequencia(sequencia);
		
		diagnostico = new EpeDiagnostico();
		diagnostico.setId(diagnosticoId);
		
		if (snbGnbSeq != null && snbSequencia != null && sequencia != null) {
			
			EpeDiagnosticoId id = new EpeDiagnosticoId();
			id.setSnbGnbSeq(snbGnbSeq);
			id.setSnbSequencia(snbSequencia);
			id.setSequencia(sequencia);
			
			diagnostico = prescricaoEnfermagemFacade.obterDiagnosticoPorChavePrimaria(id);

			if (diagnostico.getSituacao().equals(DominioSituacao.A)) {
				diagnosticoSituacao = true;
			} else {
				diagnosticoSituacao = false;
			}
		}
		// Preenche suggestions
		grupo = prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(snbGnbSeq);
		EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId();
		idSubgrupo.setGnbSeq(snbGnbSeq);
		idSubgrupo.setSequencia(snbSequencia);
		subGrupo = prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(idSubgrupo);
	
	}

	public String gravar() {
		try {
			if (diagnosticoSituacao) {
				diagnostico.setSituacao(DominioSituacao.A);
			} else {
				diagnostico.setSituacao(DominioSituacao.I);
			}
			Boolean insert = diagnostico.getId().getSequencia() == null;
			
			if(diagnostico.getSubgrupoNecesBasica() == null){
				diagnostico.setSubgrupoNecesBasica(subGrupo);
			}
			
			prescricaoEnfermagemApoioFacade.persistirDiagnostico(diagnostico);
			exibirMensagemSucessoOperacao(insert);
			return RETORNAR_LIST;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private void exibirMensagemSucessoOperacao(Boolean insert) {
		if (insert) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MANTER_DIAGNOSTICO", diagnostico.getDescricao());
		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MANTER_DIAGNOSTICO", diagnostico.getDescricao());
		}
	}

	public String redirecionaDiagnosticosEtiologias() {
		indSituacao = diagnostico.getSituacao().toString();
		return REDIRECIONA_DIAGNOSTICOS_ETIOLOGIAS_CRUD;
	}

	public String redirecionaDiagnosticosSinaisSintomas() {
		indSituacao = diagnostico.getSituacao().toString();
		return REDIRECIONA_DIAGNOSTICOS_SINAIS_SINTOMAS_CRUD;
	}

	public String cancelar() {
		diagnostico = null;
		return RETORNAR_LIST;
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

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getCameFrom() {
		return cameFrom;
	}
}
