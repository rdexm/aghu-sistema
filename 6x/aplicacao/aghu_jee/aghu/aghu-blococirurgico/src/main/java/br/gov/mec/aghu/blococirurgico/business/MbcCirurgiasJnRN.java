package br.gov.mec.aghu.blococirurgico.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiaJnDAO;
import br.gov.mec.aghu.model.MbcCirurgiaJn;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável por prover os métodos de negócio de MbcCirurgias.
 * 
 * @autor foliveira
 * 
 */
@Stateless
public class MbcCirurgiasJnRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasJnRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiaJnDAO mbcCirurgiaJnDAO;


	private static final long serialVersionUID = -9001235811703398140L;

	/**
	 * TRIGGER "AGH".MBCT_CRG_ARU
	 * @param elemento
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void posAtualizar(MbcCirurgias elemento, MbcCirurgias old) throws BaseException {
		if (this.verificarModificadoMbcCirurgiaJnParte1(elemento, old)
				|| this.verificarModificadoMbcCirurgiaJnParte2(elemento, old)
				|| this.verificarModificadoMbcCirurgiaJnParte3(elemento, old)
				|| this.verificarModificadoMbcCirurgiaJnParte4(elemento, old)
				){	
			
			this.inserirMbcCirurgiaJn(old, DominioOperacoesJournal.UPD);
			
		}
		
	}	
	
	public boolean verificarModificadoMbcCirurgiaJnParte1(MbcCirurgias elemento, MbcCirurgias old) {
		if (CoreUtil.modificados(old.getSeq(), elemento.getSeq())
				|| CoreUtil.modificados(old.getDataDigitacaoNotaSala(), elemento.getDataDigitacaoNotaSala())
				|| CoreUtil.modificados(old.getDataEntradaSala(), elemento.getDataEntradaSala())
				|| CoreUtil.modificados(old.getDataEntradaSr(), elemento.getDataEntradaSr())
				|| CoreUtil.modificados(old.getDataFimAnestesia(), elemento.getDataFimAnestesia())
				|| CoreUtil.modificados(old.getDataFimCirurgia(), elemento.getDataFimCirurgia())
				|| CoreUtil.modificados(old.getDataInicioAnestesia(), elemento.getDataInicioAnestesia())
				|| CoreUtil.modificados(old.getDataInicioCirurgia(), elemento.getDataInicioCirurgia())
				|| CoreUtil.modificados(old.getDataInicioOrdem(), elemento.getDataInicioOrdem())
				|| CoreUtil.modificados(old.getDataPrevisaoFim(), elemento.getDataPrevisaoFim())
				){
			return true;
		}
		return false;
	}
	
	public boolean verificarModificadoMbcCirurgiaJnParte2(MbcCirurgias elemento, MbcCirurgias old) {
		if (CoreUtil.modificados(old.getDataPrevisaoInicio(), elemento.getDataPrevisaoInicio())
				|| CoreUtil.modificados(old.getDataSaidaSala(), elemento.getDataSaidaSala())
				|| CoreUtil.modificados(old.getDataSaidaSr(), elemento.getDataSaidaSr())
				|| CoreUtil.modificados(old.getDataUltimaAtualizacaoNotaSala(), elemento.getDataUltimaAtualizacaoNotaSala())
				|| CoreUtil.modificados(old.getAgenda(), elemento.getAgenda())
				|| CoreUtil.modificados(old.getAplicaListaCirurgiaSegura(), elemento.getAplicaListaCirurgiaSegura())
				|| CoreUtil.modificados(old.getAsa(), elemento.getAsa())
				|| CoreUtil.modificados(old.getAtendimento(), elemento.getAtendimento())
				|| CoreUtil.modificados(old.getCentroCustos(), elemento.getCentroCustos())
				|| CoreUtil.modificados(old.getComplementoCanc(), elemento.getComplementoCanc())
				|| CoreUtil.modificados(old.getContaminacao(), elemento.getContaminacao())
				|| CoreUtil.modificados(old.getConvenioSaude(), elemento.getConvenioSaude())
				|| CoreUtil.modificados(old.getConvenioSaudePlano(), elemento.getConvenioSaudePlano())
				|| CoreUtil.modificados(old.getCriadoEm(), elemento.getCriadoEm())
				){
			return true;
		}
		return false;
	}	
	
	public boolean verificarModificadoMbcCirurgiaJnParte3(MbcCirurgias elemento, MbcCirurgias old) {
		if (CoreUtil.modificados(old.getDestinoPaciente(), elemento.getDestinoPaciente())
				|| CoreUtil.modificados(old.getDigitaNotaSala(), elemento.getDigitaNotaSala())
				|| CoreUtil.modificados(old.getDocumentoPaciente(), elemento.getDocumentoPaciente())
				|| CoreUtil.modificados(old.getEspecialidade(), elemento.getEspecialidade())
				|| CoreUtil.modificados(old.getMomentoAgenda(), elemento.getMomentoAgenda())
				|| CoreUtil.modificados(old.getMotivoAtraso(), elemento.getMotivoAtraso())
				|| CoreUtil.modificados(old.getMotivoCancelamento(), elemento.getMotivoCancelamento())
				|| CoreUtil.modificados(old.getMotivoDemoraSalaRecuperacao(), elemento.getMotivoDemoraSalaRecuperacao())
				|| CoreUtil.modificados(old.getNaturezaAgenda(), elemento.getNaturezaAgenda())
				|| CoreUtil.modificados(old.getNumeroAgenda(), elemento.getNumeroAgenda())
				|| CoreUtil.modificados(old.getOrigemIntLocal(), elemento.getOrigemIntLocal())
				|| CoreUtil.modificados(old.getOrigemPacienteCirurgia(), elemento.getOrigemPacienteCirurgia())
				|| CoreUtil.modificados(old.getPaciente(), elemento.getPaciente())
				){
			return true;
		}
		return false;
	}	
	
	public boolean verificarModificadoMbcCirurgiaJnParte4(MbcCirurgias elemento, MbcCirurgias old) {
		if (CoreUtil.modificados(old.getPrecaucaoEspecial(), elemento.getPrecaucaoEspecial())
				|| CoreUtil.modificados(old.getProjetoPesquisa(), elemento.getProjetoPesquisa())
				|| CoreUtil.modificados(old.getQuestao(), elemento.getQuestao())
				|| CoreUtil.modificados(old.getSalaCirurgica(), elemento.getSalaCirurgica())
				|| CoreUtil.modificados(old.getSciSeqp(), elemento.getSciSeqp())
				|| CoreUtil.modificados(old.getSciUnfSeq(), elemento.getSciUnfSeq())
				|| CoreUtil.modificados(old.getServidor(), elemento.getServidor())
				|| CoreUtil.modificados(old.getSituacao(), elemento.getSituacao())
				|| CoreUtil.modificados(old.getSolicitacaoCirurgiaPosEscala(), elemento.getSolicitacaoCirurgiaPosEscala())
				){
			return true;
		}
		return false;
	}		
	
	private void popularMbcCirurgiaJnParte1(MbcCirurgias original, MbcCirurgiaJn jn) throws BaseException{
		jn.setContaminacao(original.getContaminacao());
		jn.setIndDigtNotaSala(original.getDigitaNotaSala());
		jn.setIndPrecaucaoEspecial(original.getPrecaucaoEspecial());		
		jn.setIndUtlzProAzot(original.getUtilizaProAzot());
		jn.setIndUtlzO2(original.getUtilizaO2());
		jn.setIndTemDescricao(original.getTemDescricao());
		jn.setUnfSeq(original.getUnidadeFuncional() != null ? original.getUnidadeFuncional().getSeq() : null);
		jn.setAtdSeq(original.getAtendimento() != null ? original.getAtendimento().getSeq() : null);
		jn.setTempoPrevHrs(original.getTempoPrevistoHoras() != null ? original.getTempoPrevistoHoras() : null);
		jn.setAgdSeq(original.getAgenda() != null ? original.getAgenda().getSeq() : null);
	}
	
	private void popularMbcCirurgiaJnParte2(MbcCirurgias original, MbcCirurgiaJn jn) throws BaseException{
		jn.setDthrEntradaSr(original.getDataEntradaSr());
		jn.setDthrSaidaSr(original.getDataSaidaSr());
		jn.setDthrPrevFim(original.getDataPrevisaoFim());
		jn.setDthrPrevInicio(original.getDataPrevisaoInicio());
		jn.setDthrInicioOrdem(original.getDataInicioOrdem());
		jn.setDthrDigitNotaSala(original.getDataDigitacaoNotaSala());
		jn.setDthrSaidaSala(original.getDataSaidaSala());
		jn.setEspSeq(original.getEspecialidade().getSeq());
		jn.setPacCodigo(original.getPaciente().getCodigo());
		jn.setNroAgenda(original.getNumeroAgenda());
		jn.setDpaSeq(original.getDestinoPaciente() != null ? original.getDestinoPaciente().getSeq().shortValue() : null);
		jn.setTempoPrevMin(original.getTempoPrevistoMinutos() != null ? original.getTempoPrevistoMinutos().shortValue() : null);
		jn.setSpeSeq(original.getSolicitacaoCirurgiaPosEscala() != null ? original.getSolicitacaoCirurgiaPosEscala().getSeq() : null);

	}
	private void popularMbcCirurgiaJnParte3(MbcCirurgias original, MbcCirurgiaJn jn) throws BaseException{
		Integer asa =  original.getAsa() != null ? original.getAsa().getCodigo() : null;
		jn.setAsa(asa != null ? asa.shortValue() : null);
	}
	
	private void popularMbcCirurgiaJnParte4(MbcCirurgias original, MbcCirurgiaJn jn) throws BaseException {
		jn.setData(original.getData());
		jn.setDthrInicioAnest(original.getDataInicioAnestesia());
		jn.setDthrFimAnest(original.getDataFimAnestesia());
		jn.setDthrInicioCirg(original.getDataInicioCirurgia());
		jn.setDthrFimCirg(original.getDataFimCirurgia());
		jn.setMomentoAgend(original.getMomentoAgenda() != null ? original.getMomentoAgenda().toString() : null);
		jn.setUtilizacaoSala(original.getUtilizacaoSala() != null ? original.getUtilizacaoSala().toString() : null);
		
		if (original.getValorValidoCanc() != null) {
			jn.setVvcQesMtcSeq(original.getValorValidoCanc().getQuestao().getMotivoCancelamento().getSeq());
			jn.setVvcSeqp(original.getValorValidoCanc().getId().getSeqp());
			jn.setVvcQesSeqp(original.getValorValidoCanc().getQuestao().getId().getSeqp());
			jn.setMtcSeq(original.getMotivoCancelamento().getSeq());
		}
	}	
	
	private void popularMbcCirurgiaJnParte5(MbcCirurgias original, MbcCirurgiaJn jn) throws BaseException {
		jn.setCriadoEm(original.getCriadoEm());
		jn.setSeq(original.getSeq());
		jn.setOrigemPacCirg(original.getOrigemPacienteCirurgia() != null ? original.getOrigemPacienteCirurgia().toString().charAt(0) : null);

		if (original.getQuestao() != null) {
			jn.setQesSeqp(original.getQuestao().getId().getSeqp());
			jn.setQesMtcSeq(original.getQuestao().getMotivoCancelamento().getSeq());	
		}
		
		if (original.getConvenioSaudePlano() != null) {
			jn.setCspSeq(original.getConvenioSaudePlano().getId().getSeq().shortValue());
			jn.setCspCnvCodigo(original.getConvenioSaudePlano().getConvenioSaude().getCodigo());	
		}
		
		if (original.getSciUnfSeq() != null ) {
			jn.setSciUnfSeq(original.getSciUnfSeq());
			jn.setSciSeqp(original.getSciSeqp());		
		}
	}
	
	private void popularMbcCirurgiaJnParte6(MbcCirurgias original, MbcCirurgiaJn jn) throws BaseException{
		jn.setIndOverbooking(original.getOverbooking());
		jn.setSituacao(original.getSituacao().toString());
		jn.setNaturezaAgend(original.getNaturezaAgenda().toString());
		jn.setTempoUtlzO2(original.getTempoUtilizacaoO2());
		jn.setTempoUtlzProAzot(original.getTempoUtilizacaoProAzot());
		jn.setOrigemIntLocal(original.getOrigemIntLocal());
		jn.setDthrUltAtlzNotaSala(original.getDataUltimaAtualizacaoNotaSala());
		jn.setDthrEntradaSala(original.getDataEntradaSala());
		jn.setDocumentoPac(original.getDocumentoPaciente() != null ? original.getDocumentoPaciente().toString() : null);
		jn.setMsrSeq(original.getMotivoDemoraSalaRecuperacao() != null ? original.getMotivoDemoraSalaRecuperacao().getSeq() : null);
		jn.setComplementoCanc(original.getComplementoCanc() != null ? original.getComplementoCanc() : null);
		jn.setMoaSeq(original.getMotivoAtraso() != null ? original.getMotivoAtraso().getSeq() : null);
	}
	
	private void inserirMbcCirurgiaJn(MbcCirurgias original, DominioOperacoesJournal op) throws BaseException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcCirurgiaJn jn = BaseJournalFactory.getBaseJournal(op, MbcCirurgiaJn.class, servidorLogado.getUsuario());
		
		jn.setOperacao(op);
		jn.setSerMatricula(servidorLogado.getId().getMatricula());
		jn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		
		this.popularMbcCirurgiaJnParte1(original, jn);
		this.popularMbcCirurgiaJnParte2(original, jn);
		this.popularMbcCirurgiaJnParte3(original, jn);
		this.popularMbcCirurgiaJnParte4(original, jn);
		this.popularMbcCirurgiaJnParte5(original, jn);
		this.popularMbcCirurgiaJnParte6(original, jn);
		
		this.getMbcCirurgiaJnDAO().persistir(jn);
	}	


	private MbcCirurgiaJnDAO getMbcCirurgiaJnDAO() {
		return mbcCirurgiaJnDAO;
	}
}