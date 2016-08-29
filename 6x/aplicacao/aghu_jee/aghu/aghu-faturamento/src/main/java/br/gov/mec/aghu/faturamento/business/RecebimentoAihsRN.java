package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.vo.CursorVerificacaoDtNascDtInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.RetornoSMSVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatRetornaAih;
import br.gov.mec.aghu.model.FatRetornaAihId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RecebimentoAihsRN extends BaseBusiness {

	private static final long serialVersionUID = -6912621979734655961L;
	private static final Log LOG = LogFactory.getLog(RecebimentoAihsRN.class);
	private static final String CONTA_REAPRESENTADA = "2007";
	private static final String PROGRAMA_FATF_ENVIO_CTH = "FATF_ENVIO_CTH";
	private static final String IND_TIPO_LAUDO_P = "P";
	private static final String IND_TIPO_LAUDO_S = "S"; 
	private static final String MODULO = "FAT";
	
	@Inject
	private FatContasHospitalaresDAO contasHospitalaresDAO;
	
	@Inject
	private FatContasInternacaoDAO contasInternacaoDAO;
	
	@EJB
	private FatLogErrorRN fatLogErrorRN; 
	
	@EJB
	private RetornoSmsRN retornoSmsRN;
	
	@EJB
	private RetornoSmsUtil smsUtil;
	
	@EJB
	private FatRetornaAihRN fatRetornaAihRN;

	@Override
	protected Log getLogger() {
		return LOG;
	}
		
	public Integer inserir(String linha, File file) throws BaseException {	
		
		Boolean vArqEnviado = Boolean.TRUE;
		Boolean vErro = Boolean.FALSE;
		Integer vConta = 0; 
		List<RetornoSMSVO> listVO =  new ArrayList<RetornoSMSVO>();
		
		// v_laudo := substr(dados,1,13);
		Long vLaudo =  smsUtil.converterStringEmLong(StringUtils.substring(linha, 0, 13));
		// v_tipo := substr(dados,14,1);
		String vTipo = StringUtils.substring(linha, 13, 14);
		// v_cth_seq := substr(v_laudo,7,6);
		Integer vCthSeq = obtervCthSeq(linha, vLaudo.toString()); 
		
		if(!DominioSituacaoConta.R.equals(obterCursorSituacaoConta(vCthSeq))){
			// Fim Reapresentadas ETB 23012008
    		// Marina 08/05/2009
				
			if(vArqEnviado){
				// Chama a procedure para ler todos os arquivos do ano, mês e dia que foram enviados para o SUS
				listVO =  retornoSmsRN.lerArquivoEnviado(obterCthSeqApartirDoLaudo(vLaudo.toString()), file);
				vArqEnviado = Boolean.FALSE;	
			} 
			
			// Verifica se teve alteração na data de nascimento ou na data de internação
			CursorVerificacaoDtNascDtInternacaoVO voCursor = obterCursorVerificacaoDtNascDtInternacao(vCthSeq);
			
			for (RetornoSMSVO voSMS : listVO) {

				if(!CoreUtil.modificados(voSMS.getLaudo(), vLaudo)) { // verificar tipo long
					//  Verifica se teve alteração na data de Nascimento
					if(CoreUtil.modificados(voSMS.getDataNasc(), voCursor.getDataNascimento())){
						vErro = Boolean.FALSE;
						persistirFatLogError(vCthSeq, voSMS, "DATA DE NASCIMENTO DIFERENTE DA DATA DE NASCIMENTO AUTORIZADA PELO SUS");	
					}
					
					//  Verifica se teve alteração na data de Internação
					if(CoreUtil.modificados(voSMS.getDataInt(), voCursor.getDataInternacao())) {  // VERIFICAR SE TEM QUE TRUNCAR
						vErro = Boolean.FALSE;
						persistirFatLogError(vCthSeq, voSMS, "DATA DE INTERNAÇÃO DIFERENTE DA DATA DE INTERNÇÃO AUTORIZADA PELO SUS");	
					}
				}
			} // Fim Marina
			
			// Marina 12/05/2009
			if(!vErro){
				if("0".equals(vTipo)) {
					vConta = 1;
					
					Long    vSsmAut  = obterSsmAut(linha);
					Long    vCpf	 = obterCPF(linha);
					Long	vAih 	 = obterAih(linha);
					Date	vDtEmissao = obterDtEmissao(linha);
					Long    vCns	 = obterCns(linha);
					
					getLogger().info("v_cns: " + vCns.toString());
					
					FatRetornaAihId id = new FatRetornaAihId();

					id.setNroLaudo(vLaudo);
					id.setCthSeq(vCthSeq);
					id.setNroCpfAuditor(vCpf);
					id.setNroAih(vAih);
					id.setDtEmissaoAih(vDtEmissao);
					id.setCodSusAut(vSsmAut);
					id.setIndTipoLaudo(IND_TIPO_LAUDO_P);
					id.setNroCnsAuditor(vCns);
					
					fatRetornaAihRN.persistir(new FatRetornaAih(id));

				} else {
					
					Long vProcedimento	 = obterProcedimento(linha);
					Integer vQtd		 = obterQtd(linha); 
					Integer vCompetencia = obterCompetencia(linha); // Incluída por ETB em 14/05/2008
		
					FatRetornaAihId id = new FatRetornaAihId();
					id.setNroLaudo(vLaudo);
					id.setCthSeq(vCthSeq);
					id.setCodSusCma1(vProcedimento); 
					id.setCodSusCma2(vQtd);
					id.setCodSusCma3(vCompetencia);
					id.setIndTipoLaudo(IND_TIPO_LAUDO_S);
					fatRetornaAihRN.persistir(new FatRetornaAih(id));
				}
			}
			
		} else {
			
			FatLogError fatLogError = obterFatLogError(vCthSeq);
			fatLogError.setErro("CONTA NA SITUACAO DE REJEITADA");
			
			fatLogErrorRN.persistir(fatLogError);

		} // Marina 12/05/2009

		return vConta;
	}

	private void persistirFatLogError(Integer vCthSeq, RetornoSMSVO voSMS, String msgErro) throws ApplicationBusinessException {
		FatLogError fatLogError = obterFatLogError(vCthSeq);
		fatLogError.setErro(msgErro);
		fatLogError.setNomeArquivoImp(voSMS.getNomeArquivoImp());
		fatLogError.setDataArquivoImp(voSMS.getDataArquivoImp());
		
		fatLogErrorRN.persistir(fatLogError);
	}

	private Integer obterCompetencia(String linha) {
		// NVL(v_competencia,0)  -- null - alterada por ETB em 14/05/2008
		// v_competencia  := SUBSTR(dados,28,06);
		return (Integer) CoreUtil.nvl(smsUtil.converterStringEmInteger(StringUtils.substring(linha, 27, 33)), 0);
	}

	private Integer obterQtd(String linha) {
		// v_qtd := SUBSTR(dados,25,03);
		return smsUtil.converterStringEmInteger(StringUtils.substring(linha, 24, 27));
	}

	private Long obterProcedimento(String linha) {
		// v_procedimento := SUBSTR(dados,15,10);
		return smsUtil.converterStringEmLong(StringUtils.substring(linha, 14, 24));
	}

	private Long obterCns(String linha) {
		// v_cns := substr(dados,232,15);
		return smsUtil.converterStringEmLong(StringUtils.substring(linha, 231, 246));
	}

	private Date obterDtEmissao(String linha) throws ApplicationBusinessException {
		// v_dt_emissao := TO_DATE(SUBSTR(dados,194,8),'ddmmyyyy');
		return smsUtil.obterData(StringUtils.substring(linha, 193, 201));
	}

	private Long obterAih(String linha) {
		//  v_aih  := SUBSTR(dados,106,13);
		return smsUtil.converterStringEmLong(StringUtils.substring(linha, 105, 118));
	}

	private Long obterCPF(String linha) {
		// v_cpf :=  SUBSTR(dados,95,11);
		return smsUtil.converterStringEmLong(StringUtils.substring(linha, 94, 105));
	}

	private Long obterSsmAut(String linha) {
		// v_ssm_aut := SUBSTR(dados,85,10);
		return smsUtil.converterStringEmLong(StringUtils.substring(linha, 84, 94));
	}

	private FatLogError obterFatLogError(Integer vCthSeq) {
		FatLogError fatLogError =  new FatLogError();
		fatLogError.setModulo(MODULO);
		fatLogError.setPrograma(PROGRAMA_FATF_ENVIO_CTH);
		fatLogError.setCthSeq(vCthSeq);
		return fatLogError;
	}
	
	private CursorVerificacaoDtNascDtInternacaoVO obterCursorVerificacaoDtNascDtInternacao(Integer cthSeq) {
		CursorVerificacaoDtNascDtInternacaoVO vo = contasInternacaoDAO.obterCursorVerificacaoDtNascDtInternacao(cthSeq);
		
		if (vo != null) {
			// TO_NUMBER(TO_CHAR(cth.dt_int_administrativa, 'YYYYMMDD')) dt_int
			vo.setDataInternacao(DateUtil.truncaData(vo.getDataInternacao()));
			// TO_NUMBER(TO_CHAR(aip.dt_nascimento, 'YYYYMMDD')) dt_nas
			vo.setDataNascimento(DateUtil.truncaData(vo.getDataNascimento()));
		} else {
			vo =  new CursorVerificacaoDtNascDtInternacaoVO();
		}

		return vo;
	}
	
	private Integer obtervCthSeq(String linha, String laudo) throws BaseException {
		// v_cth_seq := substr(v_laudo,7,6);
		Integer vCthSeq = obterCthSeqApartirDoLaudo(laudo);

		//  Reapresentadas ETB 23012008		
		//  if substr(dados,1,4) = '2007' then -- conta reapresentada
		if(CONTA_REAPRESENTADA.equals(StringUtils.substring(linha, 0, 4))){
			FatContasHospitalares contasHospitalares  = contasHospitalaresDAO.obterPorContaRepresentada(vCthSeq);
			if(contasHospitalares != null){
				vCthSeq = contasHospitalares.getSeq();
			}
		} 
				
		return vCthSeq;	
	}

	private Integer obterCthSeqApartirDoLaudo(String laudo) {
		// v_cth_seq := substr(v_laudo,7,6);
		String strCthSeq = StringUtils.substring(laudo, 6, 12);
		return smsUtil.converterStringEmInteger(strCthSeq);
	}
	
	private DominioSituacaoConta obterCursorSituacaoConta(Integer vCthSeq){
		// teste se a conta esta rejeitada    
		FatContasHospitalares contasHospitalares = contasHospitalaresDAO.obterPorChavePrimaria(vCthSeq);
		return contasHospitalares != null ? contasHospitalares.getIndSituacao() : null;
	}

}
