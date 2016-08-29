package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoDiagsLacunaController extends ActionController {


	private static final long serialVersionUID = -6468305308819422572L;

	private static final String TEXTO_PADRAO_DIAGNOSTICO = "textoPadraoDiagnostico";

	private static final String TEXTO_DIAGNOSTICO_LACUNA = "textoDiagnosticoLacuna";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	private Short aelTextoPadraoDiagsLuhSeq;

	private Short aelTextoPadraoDiagsSeqp;

	private List<AelGrpDiagLacunas> lista;

	// Avo
	private AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags;

	// Pai
	private AelTextoPadraoDiags aelTextoPadraoDiags;

	// Filho
	private AelGrpDiagLacunas aelGrpDiagLacunas;

	private boolean editando;
	
	private AelGrpDiagLacunasId idExcluir;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String inicio() {
	 

		editando = false;
		
		aelTextoPadraoDiags = examesPatologiaFacade.obterAelTextoPadraoDiags(new AelTextoPadraoDiagsId(aelTextoPadraoDiagsLuhSeq, aelTextoPadraoDiagsSeqp));

		if(aelTextoPadraoDiags == null){
			apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			return voltar();
		}
		
		criarObjetoInsersao();

		pesquisar();
		return null;
	
	}

	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelGrpDiagLacunasPorTextoPadraoDiags(aelTextoPadraoDiagsLuhSeq, aelTextoPadraoDiagsSeqp,
				null);
	}

	public void gravar() {
		try {
			final boolean novo = this.aelGrpDiagLacunas.getId() == null || this.aelGrpDiagLacunas.getId().getSeqp() == null;
			examesPatologiaFacade.persistirAelGrpDiagLacunas(aelGrpDiagLacunas);

			apresentarMsgNegocio(Severity.INFO, novo ? "MENSAGEM_AEL_GRP_DIAGNOSTICO_LACUNAS_INSERT_SUCESSO" : 
													   "MENSAGEM_AEL_GRP_DIAGNOSTICO_LACUNAS_UPDATE_SUCESSO",
													   aelGrpDiagLacunas.getLacuna());
			if (!novo) {
				this.cancelarEdicao();
			}

			this.criarObjetoInsersao();

		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}

	public void editar(final AelGrpDiagLacunas aelGrpDiagLacunas) {
		this.editando = true;
		this.aelGrpDiagLacunas = aelGrpDiagLacunas;
	}

	public void cancelarEdicao() {
		this.editando = false;
		this.criarObjetoInsersao();
	}
	
	private void criarObjetoInsersao() {
		this.aelGrpDiagLacunas = new AelGrpDiagLacunas();
		this.aelGrpDiagLacunas.setAelTextoPadraoDiags(this.aelTextoPadraoDiags);
	}

	public void excluir() {
		try {
			final AelGrpDiagLacunas aelGrpDiagLacunasExcluir = examesPatologiaFacade.obterAelGrpDiagLacunas(idExcluir);
			idExcluir = null;
			
			if(aelGrpDiagLacunasExcluir == null){
				apresentarMsgNegocio(Severity.ERROR,"REGISTRO_NULO_EXCLUSAO");
				pesquisar();
				return;
			}
			
			this.examesPatologiaFacade.excluirAelGrpDiagLacunas(aelGrpDiagLacunasExcluir.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AEL_GRP_DIAGNOSTICO_LACUNAS_DELETE_SUCESSO",
					aelGrpDiagLacunasExcluir.getLacuna());

			this.pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void ativarInativar(final AelGrpDiagLacunas elemento) {
		try {
			String msg;
			if(DominioSituacao.A.equals(elemento.getIndSituacao())){
				elemento.setIndSituacao(DominioSituacao.I);
				msg = "MENSAGEM_AEL_GRP_DIAGNOSTICO_LACUNAS_INATIVADO_SUCESSO";
				
			} else {
				elemento.setIndSituacao(DominioSituacao.A);
				msg = "MENSAGEM_AEL_GRP_DIAGNOSTICO_LACUNAS_ATIVADO_SUCESSO";
			}
			
			examesPatologiaFacade.persistirAelGrpDiagLacunas(elemento);

			apresentarMsgNegocio(Severity.INFO, msg, elemento.getLacuna());

			criarObjetoInsersao();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		pesquisar();
	}
	
	public String detalharGrupoDiagsLacuna(){
		return TEXTO_DIAGNOSTICO_LACUNA;
	}

	public String voltar() {
		return TEXTO_PADRAO_DIAGNOSTICO;
	}

	public Short getAelTextoPadraoDiagsLuhSeq() {
		return aelTextoPadraoDiagsLuhSeq;
	}

	public void setAelTextoPadraoDiagsLuhSeq(final Short aelTextoPadraoDiagsLuhSeq) {
		this.aelTextoPadraoDiagsLuhSeq = aelTextoPadraoDiagsLuhSeq;
	}

	public Short getAelTextoPadraoDiagsSeqp() {
		return aelTextoPadraoDiagsSeqp;
	}

	public void setAelTextoPadraoDiagsSeqp(final Short aelTextoPadraoDiagsSeqp) {
		this.aelTextoPadraoDiagsSeqp = aelTextoPadraoDiagsSeqp;
	}

	public List<AelGrpDiagLacunas> getLista() {
		return lista;
	}

	public void setLista(final List<AelGrpDiagLacunas> lista) {
		this.lista = lista;
	}

	public AelTextoPadraoDiags getAelTextoPadraoDiags() {
		return aelTextoPadraoDiags;
	}

	public void setAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) {
		this.aelTextoPadraoDiags = aelTextoPadraoDiags;
	}

	public AelGrpDiagLacunas getAelGrpDiagLacunas() {
		return aelGrpDiagLacunas;
	}

	public void setAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas) {
		this.aelGrpDiagLacunas = aelGrpDiagLacunas;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(final boolean editando) {
		this.editando = editando;
	}

	public AelGrpTxtPadraoDiags getAelGrpTxtPadraoDiags() {
		return this.aelGrpTxtPadraoDiags;
	}

	public void setAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		this.aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiags;
	}

	public AelGrpDiagLacunasId getIdExcluir() {
		return idExcluir;
	}

	public void setIdExcluir(AelGrpDiagLacunasId idExcluir) {
		this.idExcluir = idExcluir;
	}

}
