package br.gov.mec.aghu.blococirurgico.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RegimeProcedimentoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.TempoSalaAgendaVO;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 *  Classe responsável pelas regras de FORMS para #22338
 *  @author rpanassolo
 */
@Stateless
public class MbcAgendaProcedimentoON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaProcedimentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;


	@EJB
	private MbcAgendaProcedimentoRN mbcAgendaProcedimentoRN;

	private static final long serialVersionUID = 4918335814295466329L;
	
	private static final Integer TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS = 4;

	public enum MbcAgendaProcedimentoONExceptionCode implements BusinessExceptionCode {
		MBC_00804, MBC_00974
	}

	
	
	public void gravarAgendaProcedimento(final MbcAgendas agendaOriginal, final List<MbcAgendaProcedimento> agendaProcedimentos, final List<MbcAgendaProcedimento> agendaProcedimentosRemovidas,
			final MbcAgendas agenda) throws BaseException {
		
		if(agendaProcedimentosRemovidas != null && !agendaProcedimentosRemovidas.isEmpty()) {
			for (int i = 0; i < agendaProcedimentosRemovidas.size(); i++) {
				MbcAgendaProcedimento agendaProcedimento = getMbcAgendaProcedimentoDAO().obterProcedimentoCirurgicoPorAgdSeqEprPciSeqEspSeq(agendaOriginal.getSeq(), 
						agendaProcedimentosRemovidas.get(i).getId().getEprPciSeq(), agendaProcedimentosRemovidas.get(i).getId().getEprEspSeq());
				if (agendaProcedimento != null) { 
					this.getMbcAgendaProcedimentoRN().deletar(agendaProcedimento);
				}
			}
		}

		if(agendaProcedimentos != null && ! agendaProcedimentos.isEmpty()){
			for (final MbcAgendaProcedimento agendaProcedimento : agendaProcedimentos ) {
				agendaProcedimento.setMbcAgendas(agenda);
				this.validarQtdeAgendaProcedimento(agendaProcedimento);
				MbcAgendaProcedimento oldAgendaProcedimento = null;
				if (agendaOriginal != null && agendaOriginal.getAgendasprocedimentos() != null && agendaOriginal.getAgendasprocedimentos().contains(agendaProcedimento)) {
					oldAgendaProcedimento = agendaOriginal.getAgendasprocedimentos().get(agendaOriginal.getAgendasprocedimentos().indexOf((agendaProcedimento)));
					if(CoreUtil.modificados(agendaProcedimento.getQtde(), oldAgendaProcedimento.getQtde())){
						
						MbcAgendaProcedimento procedimento = getMbcAgendaProcedimentoDAO().obterProcedimentoCirurgicoPorAgdSeqEprPciSeqEspSeq(agendaOriginal.getSeq(), 
								agendaProcedimento.getId().getEprPciSeq(), agendaProcedimento.getId().getEprEspSeq());
						
						agendaProcedimento.setProcedimentoCirurgico(procedimento.getProcedimentoCirurgico());
						agendaProcedimento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
						agendaProcedimento.setCriadoEm(procedimento.getCriadoEm());
						agendaProcedimento.setId(procedimento.getId());
						agendaProcedimento.setMbcAgendas(agenda);
						agendaProcedimento.setVersion(procedimento.getVersion());
						agendaProcedimento.setMbcEspecialidadeProcCirgs(procedimento.getMbcEspecialidadeProcCirgs());
						agendaProcedimento.setQtde(agendaProcedimento.getQtde());
						
						this.getMbcAgendaProcedimentoRN().persistirAgendaProcedimentos(oldAgendaProcedimento, agendaProcedimento);
					}
				} else {
					agendaProcedimento.setId(null);
					getMbcAgendaProcedimentoDAO().desatachar(agendaProcedimento);
					this.getMbcAgendaProcedimentoRN().persistirAgendaProcedimentos(oldAgendaProcedimento, agendaProcedimento);
				}
				
			}
		}
	}
	
	
	
	/**
	 * ON0
	 * 
	 * @throws ApplicationBusinessException
	 */
	
	public void validarAgendaProcedimentoAdicionadoExistente(List<MbcAgendaProcedimento> agendaProcedimentoList,MbcAgendaProcedimento agendaProcedimento)throws ApplicationBusinessException {
		for (MbcAgendaProcedimento mbcAgendaProcedimento : agendaProcedimentoList) {
			if(agendaProcedimento.getProcedimentoCirurgico().getSeq().equals(mbcAgendaProcedimento.getProcedimentoCirurgico().getSeq())){
				throw new ApplicationBusinessException(MbcAgendaProcedimentoONExceptionCode.MBC_00804);
			}
		}
	}
	
	/**
	 * ON1
	 * @throws ParseException 
	 * @ORADB P_VALIDA_TEMPO_MINIMO
	 * retorna date com o tempo minimo seja do procedimento ou da sala
	 * 
	 */
	public TempoSalaAgendaVO validaTempoMinimo(Date tempoSala,VMbcProcEsp procEsp ) throws ParseException{
		SimpleDateFormat formatacaoTela = new SimpleDateFormat("HH:mm");
		TempoSalaAgendaVO tempoSalaVO = new TempoSalaAgendaVO();
		
		if(tempoSala == null && procEsp != null && procEsp.getTempoMinimo() == null){
			return null;
		}
		
		if(tempoSala != null &&  procEsp != null && procEsp.getTempoMinimo() == null){
			tempoSalaVO.setTempo(tempoSala);
			return tempoSalaVO;
		}
		
		if(tempoSala == null && procEsp.getTempoMinimo() != null){
			tempoSalaVO.setTempo(retornaDataFormatadaBanco(procEsp));
			return tempoSalaVO; 	
		}
		
		Integer procMinutos = retornaTempoProcedimentoMinutos(procEsp);
		Integer tempoSalaMinutos = retornaTempoSalaMinutos(tempoSala);
		
		if(tempoSalaMinutos < procMinutos) {
			tempoSalaVO.setInfo(Severity.INFO);
			tempoSalaVO.setMensagem("MENSAGEM_ALTERACAO_TEMPO_SALA");
			tempoSalaVO.setTempo(retornaDataFormatadaBanco(procEsp));
			tempoSalaVO.setTempoSalaFormatada(formatacaoTela.format(tempoSala));
			tempoSalaVO.setDataFormatada(formatacaoTela.format(retornaDataFormatadaBanco(procEsp)));
			tempoSalaVO.setDescricaoProcedimento(procEsp.getDescricao());
			
			return tempoSalaVO;
		}
		
		tempoSalaVO.setTempo(tempoSala);
		return tempoSalaVO;
	}
	
	private Date retornaDataFormatadaBanco(VMbcProcEsp procEsp) throws ParseException{
		StringBuffer strTempoMinimo = retornaTempoMinimo(procEsp);
		DateFormat formatacaoBanco = new SimpleDateFormat("HHmm");
		return formatacaoBanco.parse(strTempoMinimo.toString());
	}
	
	
	private Integer retornaTempoProcedimentoMinutos(VMbcProcEsp procEsp){
		StringBuffer strTempoMinimo = retornaTempoMinimo(procEsp);
		String strHora = strTempoMinimo.substring(0, 2);
		String strMinuto = strTempoMinimo.substring(2, TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS);
		return (Short.valueOf(strHora) * Short.valueOf("60")) + Short.valueOf(strMinuto);
	}
	
	private StringBuffer retornaTempoMinimo(VMbcProcEsp procEsp){
		StringBuffer strTempoMinimo = new StringBuffer(procEsp.getTempoMinimo().toString());
		
		while (strTempoMinimo.length() < TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS) {
			// Coloca zeros a esquerda
			strTempoMinimo.insert(0, "0");
		}
		return strTempoMinimo;
	}
	
	
	private Integer retornaTempoSalaMinutos(Date tempoSala){
		GregorianCalendar gc2 = new GregorianCalendar();
		gc2.setTime(tempoSala);
		return ((gc2.get(Calendar.HOUR_OF_DAY)*60) + gc2.get(Calendar.MINUTE));
	}
	
	/**
	 * ON2
	 * retorna true se prescisa alterar o campo regime sus com o valor do regime sus do procedimento selecionado
	 * @ORADB P_POPULA_REGIME_SUS
	 *  
	 */
	public RegimeProcedimentoAgendaVO populaRegimeSus(DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc, VMbcProcEsp procEsp){
		RegimeProcedimentoAgendaVO vo = new RegimeProcedimentoAgendaVO();
		
		if(procEsp.getRegimeProcedSus() != null && dominioRegimeProc != null){
			if(!procEsp.getRegimeProcedSus().getCodigo().equals(dominioRegimeProc.getCodigo())){
				vo.setSeveridade(Severity.INFO);
				vo.setMensagem("MENSAGEM_ALTERACAO_REGIME");
				vo.setRegime(procEsp.getRegimeProcedSus());
				vo.setDescricaoRegime(dominioRegimeProc.getDescricao());
				vo.setDescricaoRegimeProcedSus(procEsp.getRegimeProcedSus().getDescricao());
				vo.setDescricaoProc(procEsp.getDescricao());
				return vo;
			}
		}else{
			if(procEsp.getRegimeProcedSus() != null && dominioRegimeProc == null){
				vo.setRegime(procEsp.getRegimeProcedSus());
				return vo;
			}
		}
		vo.setRegime(dominioRegimeProc);
		return vo;
	}
	
	/**
	 * ON3
	 * @ORADB MBC_AGT_CK1
	 *  
	 */	
	public void validarQtdeAgendaProcedimento(MbcAgendaProcedimento agendaProcedimento)throws ApplicationBusinessException {
		if(agendaProcedimento.getQtde()== null || agendaProcedimento.getQtde()==0 ){
			throw new ApplicationBusinessException(MbcAgendaProcedimentoONExceptionCode.MBC_00974,agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getDescricao());	
		}
		
	}
	
	public MbcAgendaProcedimentoRN getMbcAgendaProcedimentoRN(){
		return mbcAgendaProcedimentoRN;
	}
	
	protected MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO() {
		return mbcAgendaProcedimentoDAO;
	}
	
}
