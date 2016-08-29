package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ConsultaDescricaoCirurgicaPolRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaDescricaoCirurgicaPolRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	private static final long serialVersionUID = 263362079167620516L;

	/**
	 * ORADB:MPMC_IDA_MES_DIA_REF
	 * 
	 * @param dtNascimento
	 * @param dataCirurgia
	 * @return
	 * RN4
	 */
	public String buscarIdade(Date dtNascimento, Date dataCirurgia) {
		
		String tempo = "anos";
		String tempoMes = "meses";
		Integer idadeMes = null;
		String idadeFormat = null;
		if (dtNascimento != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(dtNascimento);
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(dataCirurgia);
			// Obtém a idade baseado no ano
			Integer idadeNum = dataCalendario.get(Calendar.YEAR)
					- dataNascimento.get(Calendar.YEAR);
			// dataNascimento.add(Calendar.YEAR, idadeNum);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento
					.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento
							.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			if (idadeNum == 1) {
				tempo = "ano";
			}
			
			if (dataCalendario.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeMes = dataCalendario.get(Calendar.MONTH)
						+ (11 - dataNascimento.get(Calendar.MONTH));
			} else {
				idadeMes = dataCalendario.get(Calendar.MONTH)
						- dataNascimento.get(Calendar.MONTH);
				if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento
						.get(Calendar.DAY_OF_MONTH)) {
					idadeMes--;
				}
			}
			
			if(idadeMes<0){
				if (12+(idadeMes) == 1) {
					tempoMes = 12+(idadeMes) + " mês";
				} else {
					tempoMes = 12+(idadeMes) + " meses";
				}	
			}else{
				if (idadeMes < 2) {
					tempoMes = idadeMes + " mês";
				} else {
					tempoMes = idadeMes + " meses";
				}
			}

			if (idadeNum < 1) {
				if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
					int lastDayMonth = dataNascimento.getActualMaximum(Calendar.DAY_OF_MONTH);
					idadeNum = (lastDayMonth - dataNascimento.get(Calendar.DAY_OF_MONTH))
							+ dataCalendario.get(Calendar.DAY_OF_MONTH);
				} else {
					idadeNum = dataCalendario.get(Calendar.DAY_OF_MONTH) - dataNascimento.get(Calendar.DAY_OF_MONTH);
				}
				idadeNum++;
				if (idadeNum == 1) {
					tempo = "dia";
				} else {
					tempo = "dias";
				}
				idadeFormat = tempoMes + " " + idadeNum + " " + tempo;
			}else{
				idadeFormat = idadeNum + " " + tempo + " " + tempoMes;
			}
		}
		return idadeFormat;
	}
	
	/**
	 * ORADB:CF_RESP_NOVO2Formula
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @return
	 * RN5
	 *  
	 */
	public String buscarResponsavel(Short vinCodigo, Integer matricula) throws BaseException  {
		
		StringBuilder vResponsavel = new StringBuilder();
		if (matricula != null && vinCodigo != null)  {

			BuscaConselhoProfissionalServidorVO consProfServidorVO = getPrescricaoMedicaFacade().buscaConselhoProfissionalServidorVO(matricula, vinCodigo, Boolean.FALSE);

			if (consProfServidorVO.getNome()!=null){
				vResponsavel.append(consProfServidorVO.getNome()).append(' ');
			}
			if (consProfServidorVO.getSiglaConselho()!=null){
				vResponsavel.append(consProfServidorVO.getSiglaConselho()).append(' ');
			}
			if (consProfServidorVO.getNumeroRegistroConselho()!=null){
				vResponsavel.append(consProfServidorVO.getNumeroRegistroConselho());
			}
			
			return vResponsavel.toString();
		}
		
		return null;
	}
	
	/**
	 * ORADB:R_8FormatTrigger
	 * 
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * RN6
	 */
	public Boolean verificarBoolFDC(Integer crgSeq, Short seqp) {
		
		Integer maxFDCSeq = getBlocoCirurgicoFacade().obterMaxFDCSeqp(crgSeq, seqp);
		
		if (maxFDCSeq != null && maxFDCSeq > 0){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * ORADB:R_27FormatTrigger
	 * 
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * RN7
	 */
	public Boolean verificarBoolNTA(Integer crgSeq, Short seqp) {
		Integer maxNTASeq = getBlocoCirurgicoFacade().obterMaxNTASeqp(crgSeq, seqp);
		
		if (maxNTASeq != null && maxNTASeq > 0){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * ORADB:CF_MATRICULA_NTA
	 * 
	 * RN8
	 */
	public Integer verificarMatriculaNTA(Integer ntaSerMatricula) {
		if(ntaSerMatricula == null){
			return 0;
		}else{
			return ntaSerMatricula;
		}
	}
	
	/**
	 * ORADB:M_6FormatTrigger
	 * 
	 * RN9
	 */
	public Boolean verificarM6(Integer matriculaNTA) {
		
		if (matriculaNTA == 0){
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * ORADB:M_2FormatTrigger
	 * 
	 * RN10
	 */
	public Boolean verificarApresentaLabelResp(String nomeResp) {
		
		if (nomeResp == null){
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * ORADB:M_1FormatTrigger
	 * 
	 * @param projeto
	 * @return
	 * RN11
	 */
	public Boolean verificarApresentaLabelProjeto(String projeto) {
		
		if (projeto == null){
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * ORADB:CF_1Formula
	 * 
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * RN12
	 */
	public String buscarNumDesenho(Integer crgSeq, Short seqp, Integer pNumero) {
		
		Integer maxFDCSeq = getBlocoCirurgicoFacade().obterMaxFDCSeqp(crgSeq, seqp);
		
		String retorno;
		
		if (maxFDCSeq != null && maxFDCSeq > 0){
			pNumero++;
			retorno = pNumero + ".";
		}else {
			retorno = null;
		}
		
		return retorno;
	}
	
	/**
	 * ORADB:F_17FormatTriggger
	 * 
	 * @param crgSeq
	 * @return
	 * Formatacao a ser verificada no momento da impressao
	 * RN13
	 */
	
	
	/**
	 * ORADB:CF_2Formula
	 * 
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * RN14
	 */
	public String buscarNumNotasAdicionais(Integer crgSeq, Short seqp, Integer pNumero) {
		Integer maxNTASeq = getBlocoCirurgicoFacade().obterMaxNTASeqp(crgSeq, seqp);
		
		String retorno;
		
		if (maxNTASeq != null && maxNTASeq > 0){
			pNumero = pNumero + 1;
			retorno = pNumero + ".";
		}else {
			retorno = null;
		}
		
		return retorno;
	}
	
	/**
	 * ORADB: F_7FormatTrigger
	 * 
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * RN15
	 */
	public Boolean verificarApresentaTitulo(Integer crgSeq, Short seqp) {
		
		Integer ntaSeqp = getBlocoCirurgicoFacade().obterNTASeqp(crgSeq, seqp);
		
		if (ntaSeqp == null) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * ORADB:CF_LEITO_RODAPEFormula
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @return
	 * RN16
	 */
	public String buscarLeitoRodape(AghAtendimentos atendimento) {
		return getFarmaciaFacade().obterLocalizacaoPacienteParaRelatorio(atendimento); 
	}
	
	/**
	 * ORADB:MBCC_BUSCA_CONSELHO
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @return
	 * RN20
	 *  
	 */
	public String buscarConselho(Short vinCodigo, Integer matricula) throws ApplicationBusinessException {
		
		if (matricula != null && vinCodigo != null)  {

			ConselhoProfissionalServidorVO consProfServidorVO = getRegistroColaboradorFacade().buscaConselhoProfissional(matricula, vinCodigo);
			
			if(consProfServidorVO != null){
				String sigla = consProfServidorVO.getSiglaConselho();
				String numeroRegistro = consProfServidorVO.getNumeroRegistroConselho();
				String vResponsavel = "";
				
				if(sigla != null && numeroRegistro != null){
					vResponsavel =  sigla + ": " + numeroRegistro;
				}
	
				return vResponsavel;
			}
		}
		
		return null;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
}
