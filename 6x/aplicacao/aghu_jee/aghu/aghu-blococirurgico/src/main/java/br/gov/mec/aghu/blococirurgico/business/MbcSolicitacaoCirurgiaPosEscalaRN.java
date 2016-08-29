package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcSolicCirgPosEscalaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoCirurgiaPosEscalaDAO;
import br.gov.mec.aghu.model.MbcSolicCirgPosEscalaJn;
import br.gov.mec.aghu.model.MbcSolicitacaoCirurgiaPosEscala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcSolicitacaoCirurgiaPosEscalaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcSolicitacaoCirurgiaPosEscalaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSolicitacaoCirurgiaPosEscalaDAO mbcSolicitacaoCirurgiaPosEscalaDAO;

	@Inject
	private MbcSolicCirgPosEscalaJnDAO mbcSolicCirgPosEscalaJnDAO;



	/**
	 * 
	 */
	private static final long serialVersionUID = 8728611870451296177L;

	protected enum MbcSolicitacaoCirurgiaPosEscalaRNExceptionCode implements BusinessExceptionCode {
		MBC_01201;
	}

	public void atualizar(MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala) throws BaseException {
		atualizar(solicitacaoCirurgiaPosEscala, true);
	}
	
	public void atualizar(MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala, boolean flush) throws BaseException {
		this.preAtualizar(solicitacaoCirurgiaPosEscala);
		this.getMbcSolicitacaoCirurgiaPosEscalaDAO().atualizar(solicitacaoCirurgiaPosEscala);
		
		if(flush){
			this.getMbcSolicitacaoCirurgiaPosEscalaDAO().flush();
		}
		
		this.posAtualizar(solicitacaoCirurgiaPosEscala);
	}

	/**
	 * TRIGGER "AGH".MBCT_SPE_ARU
	 * @param elemento
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void posAtualizar(MbcSolicitacaoCirurgiaPosEscala elemento) throws BaseException {
		MbcSolicitacaoCirurgiaPosEscala old = this.getMbcSolicitacaoCirurgiaPosEscalaDAO().obterOriginal(elemento);
		
		if (CoreUtil.modificados(old.getSolicitacoesEspeciais(), elemento.getSolicitacoesEspeciais())
				|| CoreUtil.modificados(old.getDataInclusaoLista(), elemento.getDataInclusaoLista())
				|| CoreUtil.modificados(old.getHoraPrevisaoInicio(), elemento.getHoraPrevisaoInicio())
				|| CoreUtil.modificados(old.getTempoPrevistoHora(), elemento.getTempoPrevistoHora())
				|| CoreUtil.modificados(old.getTempoPrevistoMinuto(), elemento.getTempoPrevistoMinuto())
				|| CoreUtil.modificados(old.getMedicoSolicitante(), elemento.getMedicoSolicitante())
				|| CoreUtil.modificados(old.getConvenioSaudePlano(), elemento.getConvenioSaudePlano())
				|| CoreUtil.modificados(old.getMbcEspecialidadeProcCirgs(), elemento.getMbcEspecialidadeProcCirgs())
				|| CoreUtil.modificados(old.getMbcProfAtuaUnidCirgs(), elemento.getMbcProfAtuaUnidCirgs())
				|| CoreUtil.modificados(old.getPucIndFuncaoProf(), elemento.getPucIndFuncaoProf())
				|| CoreUtil.modificados(old.getData(), elemento.getData())
				|| CoreUtil.modificados(old.getUnidadeFuncional(), elemento.getUnidadeFuncional())
				|| CoreUtil.modificados(old.getPaciente(), elemento.getPaciente())
				|| CoreUtil.modificados(old.getSolicitacaoCirurgiaPosEscala(), elemento.getSolicitacaoCirurgiaPosEscala())){	
			
			this.inserirMbcSolicitacaoCirurgiaPosEscalaJn(elemento, DominioOperacoesJournal.UPD);
			
		}
		
	}
	
	private void inserirMbcSolicitacaoCirurgiaPosEscalaJn(MbcSolicitacaoCirurgiaPosEscala original, DominioOperacoesJournal op) throws BaseException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcSolicCirgPosEscalaJn jn = BaseJournalFactory.getBaseJournal(op, MbcSolicCirgPosEscalaJn.class, servidorLogado.getUsuario());
		
		jn.setCriadoEm(original.getCriadoEm());
		jn.setOperacao(op);
		jn.setSerMatricula(servidorLogado.getId().getMatricula());
		jn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		jn.setSolicitacoesEspeciais(original.getSolicitacoesEspeciais());
		jn.setDthrSolicAtendida(original.getDataSolicitacaoAtendida());
		jn.setSerMatriculaAtende(original.getServidorAtendente().getId().getMatricula());
		jn.setSerVinCodigoAtende(original.getServidorAtendente().getId().getVinCodigo());
		jn.setDthrInclusaoLista(original.getDataInclusaoLista());
		jn.setIndSolicExcluida(original.getSolicitacaoExcluida().toString());
		jn.setHoraPrevInicio(original.getHoraPrevisaoInicio());
		jn.setTempoPrevHoras(original.getTempoPrevistoHora());
		jn.setTempoPrevMinutos(original.getTempoPrevistoMinuto().shortValue());
		jn.setMedicoSolicitante(original.getMedicoSolicitante());
		jn.setIndSolicAtendida(original.getSolicitacaoAtendida().toString());
		jn.setCspSeq(original.getConvenioSaudePlano().getId().getSeq().shortValue());
		jn.setEprPciSeq(original.getMbcEspecialidadeProcCirgs().getId().getPciSeq());
		jn.setEspSeq(original.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
		jn.setUnfSeq(original.getUnidadeFuncional().getSeq());
		jn.setPucUnfSeq(original.getMbcProfAtuaUnidCirgs().getId().getUnfSeq());
		jn.setPucIndFuncaoProf(original.getPucIndFuncaoProf().toString());
		jn.setSeq(original.getSeq());
		jn.setData(original.getData());
		jn.setPacCodigo(original.getPaciente().getCodigo());
		jn.setCspCnvCodigo(original.getConvenioSaudePlano().getId().getCnvCodigo());
		jn.setDthrSolicExcluida(original.getDataSolicitacaoExcluida());
		jn.setSerMatriculaExclui(original.getServidorExclusao().getId().getMatricula());
		jn.setSerVinCodigoExclui(original.getServidorExclusao().getId().getVinCodigo());
		jn.setJustifExclusao(original.getJustificativaExclusao());
		jn.setSpeSeq(original.getSolicitacaoCirurgiaPosEscala().getSeq());
		
		this.getMbcSolicCirgPosEscalaJnDAO().persistir(jn);
         
	}
	

	/**
	 * ORADB TRIGGER "AGH".MBCT_SPE_BRU
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizar(MbcSolicitacaoCirurgiaPosEscala elemento) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();


		MbcSolicitacaoCirurgiaPosEscala old = this.getMbcSolicitacaoCirurgiaPosEscalaDAO().obterOriginal(elemento);

		if(CoreUtil.modificados(old.getTempoPrevistoHora(),elemento.getTempoPrevistoHora()) 
				&& CoreUtil.modificados(old.getTempoPrevistoMinuto(),elemento.getTempoPrevistoMinuto())){

			/* Informe tempo previsto de duração da cirurgia */
			this.verificarTempo(elemento);
		}

		if(CoreUtil.modificados(old.getSolicitacaoAtendida(),elemento.getSolicitacaoAtendida())){

			if(elemento.getSolicitacaoAtendida()){
				elemento.setDataSolicitacaoAtendida(new Date());
				elemento.setServidorAtendente(servidorLogado);
			}else{
				elemento.setDataSolicitacaoAtendida(null);
				elemento.setServidorAtendente(null);
			}
		}

		if(CoreUtil.modificados(old.getSolicitacaoExcluida(),elemento.getSolicitacaoExcluida())){

			if(elemento.getSolicitacaoExcluida()){
				elemento.setDataSolicitacaoExcluida(new Date());
				elemento.setServidorExclusao(servidorLogado);
			}else{
				elemento.setDataSolicitacaoExcluida(null);
				elemento.setServidorExclusao(null);
			}
		}
		
	}

	/**
	 * ORADB PROCEDURE spe_rn.rn_spep_ver_tempo
	 * @param elemento
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException
	 */
	private void verificarTempo(MbcSolicitacaoCirurgiaPosEscala elemento)throws ApplicationBusinessException {

		if((elemento.getTempoPrevistoHora()== null || elemento.getTempoPrevistoHora()==0) 
				&& (elemento.getTempoPrevistoMinuto() == null || elemento.getTempoPrevistoMinuto()==0)){

			throw new ApplicationBusinessException(MbcSolicitacaoCirurgiaPosEscalaRNExceptionCode.MBC_01201);
		}

	}

	/**GET**/
	protected MbcSolicitacaoCirurgiaPosEscalaDAO getMbcSolicitacaoCirurgiaPosEscalaDAO() {
		return mbcSolicitacaoCirurgiaPosEscalaDAO;
	}

	private MbcSolicCirgPosEscalaJnDAO getMbcSolicCirgPosEscalaJnDAO() {
		return mbcSolicCirgPosEscalaJnDAO;
	}
	
}
