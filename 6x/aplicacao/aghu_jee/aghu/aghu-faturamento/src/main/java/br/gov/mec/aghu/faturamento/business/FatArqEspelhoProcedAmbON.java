package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatArqEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.vo.FatArqEspelhoProcedAmbVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FatArqEspelhoProcedAmbON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FatArqEspelhoProcedAmbON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatArqEspelhoProcedAmbDAO fatArqEspelhoProcedAmbDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1478552678476260299L;
	private static final String EXTENSAO = ".txt";
	private static final String ENCODE="ISO-8859-1";
	private static final String ZERO = "0";
	private static final String ASPAS = "\"";
	private static final String ASPAS_VIRGULA = "\",";
	private static final String YYYY_MM_DD = "yyyyMMdd";
	private static final String PONTO = String.valueOf('.');
	

	protected FatArqEspelhoProcedAmbDAO getFatArqEspelhoProcedAmbDAO() {
		return fatArqEspelhoProcedAmbDAO;
	}

	public String gerarArquivoBPADataSus(final FatCompetencia competencia, final Long procedimento, final Integer tctSeq) throws IOException {
		final List<FatArqEspelhoProcedAmbVO> resultAmbs = montaConsultaFormatada(competencia, procedimento, tctSeq); 
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_FAT_DCIH.toString(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			
		for (FatArqEspelhoProcedAmbVO registro : resultAmbs) {
			
			out.write(
						ASPAS + obterEmptyStringSeNull(registro.getCodigoUps(), -1, false) 		+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getCompetencia(), -1, false)	+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getCnsMedico(), -1, false)	  	+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getCodAtvProf(), -1, false)		+ ASPAS_VIRGULA +
						
						ASPAS + obterStringZeroSeNull(registro.getFolha(), 3, true)				+ ASPAS_VIRGULA +
						ASPAS + obterStringZeroSeNull(registro.getLinha(), 2, true)				+ ASPAS_VIRGULA +
						ASPAS + obterStringZeroSeNull(registro.getProcedimentoHosp(), 10, true)	+ ASPAS_VIRGULA +
	
						ASPAS + obterEmptyStringSeNull(registro.getCnsPaciente(), -1, false) 	+ ASPAS_VIRGULA +
						
						ASPAS + StringUtils.substring(obterEmptyStringSeNull(registro.getNomePaciente(), -1, false), 0, 30) + ASPAS_VIRGULA +
						
						ASPAS + (registro.getDtNascimento()	!= null ? DateUtil.obterDataFormatada(registro.getDtNascimento(), YYYY_MM_DD) : StringUtils.EMPTY) + ASPAS_VIRGULA +
								
						ASPAS + obterEmptyStringSeNull(registro.getSexo(), -1, false) 			+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getCodIbge(), -1, false) 		+ ASPAS_VIRGULA +
						ASPAS + (registro.getDataAtendimento()	!= null ? DateUtil.obterDataFormatada(registro.getDataAtendimento(), YYYY_MM_DD) : StringUtils.EMPTY) + ASPAS_VIRGULA +
						
						ASPAS + StringUtils.replace(obterEmptyStringSeNull(registro.getCid10(), -1, false), PONTO, StringUtils.EMPTY)  + ASPAS_VIRGULA +
						ASPAS + obterStringZeroSeNullFormatado(registro.getIdade(), 3, true)				+ ASPAS_VIRGULA +
						
						ASPAS + obterEmptyStringSeNull(registro.getQuantidade(), -1, false)		+ ASPAS_VIRGULA +
						ASPAS + obterStringZeroSeNullFormatado(registro.getCaraterAtendimento(), 2, true)	+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getNroAutorizacao(), -1, false) 	+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getOrigemInf(), -1, false)			+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getCompetencia(), -1, false) 		+ ASPAS_VIRGULA +
	
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						ASPAS + ZERO + ASPAS_VIRGULA +
						
						// '"'||LPAD(decode(NVL(RACA,5),5,99,raca),2,'0')||'",'||
						ASPAS + StringUtils.leftPad( 
													(registro.getRaca() != null && !("5").equals(registro.getRaca().toString())) 
														? registro.getRaca().toString() : "99"
													, 2, '0')	+ ASPAS_VIRGULA +
	
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,4) + ASPAS_VIRGULA +
						
						//'"'||LPAD(decode(NVL(NACIONALIDADE,999),999,10,NACIONALIDADE),3,'0') ||'"'  -- Marina 25/01/2011	   
						ASPAS + StringUtils.leftPad( 
													(registro.getCodigoNacionalidade() != null && !("999").equals(registro.getCodigoNacionalidade().toString())) 
														? registro.getCodigoNacionalidade().toString() : "10"
													, 3, '0')	+ ASPAS_VIRGULA +
						//parte nova
						ASPAS + ZERO + ZERO + ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getServico(), -1, false) 		+ ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getClassificacao(), -1, false) 	+ ASPAS_VIRGULA +
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,12) 						+ ASPAS_VIRGULA + //equipe
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,14) 						+ ASPAS_VIRGULA + //cnpj
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,4) 						+ ASPAS_VIRGULA + //eqp_area
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,8) 						+ ASPAS_VIRGULA + //eqp_seq
						
						
						//-- Ney 03/04/2013
						ASPAS + formatarCampoEspecialLPComZero(registro.getEndCodLogradouroPacienteBackup(),3) + ASPAS_VIRGULA + //-- lograd_pcnte 3
						ASPAS + obterEmptyStringSeNull(registro.getEndCepPaciente(), 8, false) 				   + ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getEndLogradouroPaciente(),30, false)  		   + ASPAS_VIRGULA +
						ASPAS + obterEmptyStringSeNull(registro.getEndComplementoPaciente(),10, false) 		   + ASPAS_VIRGULA + 
						ASPAS + formatarCampoEspecialLPComZero(registro.getEndNumeroPaciente(), 5)				   + ASPAS_VIRGULA + 
						ASPAS + obterEmptyStringSeNull(registro.getEndBairroPaciente(), 30, false ) 	 	   + ASPAS_VIRGULA +

						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,2)  + ASPAS_VIRGULA + //-- ddl_tel_pcnte 2
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,9)  + ASPAS_VIRGULA + //-- tel_pcnte 9 
						ASPAS + StringUtils.rightPad(StringUtils.EMPTY,40) + ASPAS + //-- email_pcnte 40
						"\n"
						
					);

		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	private String formatarCampoEspecialLPComZero(Object valor, int size) {
		if(valor != null){
			return StringUtils.leftPad(valor.toString(), size, ZERO);
		} else {
			return StringUtils.EMPTY;
		}
	}
	
	private String obterEmptyStringSeNull(final Object valor, final int size, final boolean left){
		if(valor != null){
			if(size >= 0){
				if(left){
					return StringUtils.leftPad(valor.toString(), size, StringUtils.EMPTY);
				} else {
					return StringUtils.rightPad(valor.toString(), size, StringUtils.EMPTY);
				}
			} else {
				return valor.toString();
			}
		} else {
			return StringUtils.EMPTY;
		}
	}

	private String obterStringZeroSeNullFormatado(Object valor, final int size, final boolean left){
		String result = (valor != null) ? valor.toString() : ZERO;
		if(size >= 0){
			if(left){
				return StringUtils.leftPad(result, size, ZERO);
			} else {
				return StringUtils.rightPad(result, size, ZERO);
			}
		} else {
			return result;
		}
	}
	
	private String obterStringZeroSeNull(Object valor, final int size, final boolean left){
		
		if(valor != null){
			if(size >= 0){
				if(left){
					return StringUtils.leftPad(valor.toString(), size, ZERO);
				} else {
					return StringUtils.rightPad(valor.toString(), size, ZERO);
				}
			} else {
				return valor.toString();
			}
		} else {
			return ZERO;
		}
		
//			String result = (valor != null) ? valor.toString() : ZERO;
//		if(size >= 0){
//			if(left){
//				return StringUtils.leftPad(result, size, ZERO);
//			} else {
//				return StringUtils.rightPad(result, size, ZERO);
//			}
//		} else {
//			return result;
//		}
	}

	private List<FatArqEspelhoProcedAmbVO> montaConsultaFormatada(
			FatCompetencia competencia, Long procedimento, Integer tctSeq) {
		
		final List<FatArqEspelhoProcedAmbVO> resultAmbs = getFatArqEspelhoProcedAmbDAO().obterRegistrosGeracaoArquivoBPAParte2(competencia, procedimento, tctSeq);
		//union
//		resultAmbs.addAll(getFatArqEspelhoProcedAmbDAO().obterRegistrosGeracaoArquivoBPAParte2(competencia));

		// eSchweigert 05/09/2013
		// em acordo com analista vimos que a ordenação não é primordial, logo removi código abaixo para ficar mais rápido
		
		//ordenacao
//		final ComparatorChain ordenacao = new ComparatorChain();
//		final BeanComparator ordenarProc = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.PROCEDIMENTO_HOSP_FORMATADO.toString(), new NullComparator(true));
//		final BeanComparator ordenarAtvProf = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.ATV_PROFISSIONAL.toString(), new NullComparator(true));
//		final BeanComparator ordenarTipoAtend = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.TIPO_ATENDIMENTO.toString(), new NullComparator(true));
//		final BeanComparator ordenarGrupoAtend = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.GRUPO_ATENDIMENTO.toString(), new NullComparator(true));
//		final BeanComparator ordenarFaixEtaria = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.FAIXA_ETARIA.toString(), new NullComparator(true));
//		
//		ordenacao.addComparator(ordenarProc);
//		ordenacao.addComparator(ordenarAtvProf);
//		ordenacao.addComparator(ordenarTipoAtend);
//		ordenacao.addComparator(ordenarGrupoAtend);
//		ordenacao.addComparator(ordenarFaixEtaria);
		
		//Bloco para gerar ordenacao no caso de querer comparar com o AGH
//		final BeanComparator a = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.NOME_PACIENTE.toString(), new NullComparator(true));
//		final BeanComparator b = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.DATA_ATENDIMENTO.toString(), new NullComparator(true));
//		final BeanComparator c = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.CNS_MEDICO.toString(), new NullComparator(true));
//		final BeanComparator d = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.COD_ATV_PROF.toString(), new NullComparator(true));
//		final BeanComparator e = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.PROCEDIMENTO_HOSP.toString(), new NullComparator(true));
//		final BeanComparator f = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.FOLHA.toString(), new NullComparator(true));
//		final BeanComparator g = new BeanComparator(FatArqEspelhoProcedAmbVO.Fields.LINHA.toString(), new NullComparator(true));

		//equivale na query => ORDER BY NOME_PACIENTE,data_atendimento, cns_medico,cod_atv_prof, procedimento_hosp, folha, linha		
		
//		ordenacao.addComparator(a);
//		ordenacao.addComparator(b);
//		ordenacao.addComparator(c);
//		ordenacao.addComparator(d);
//		ordenacao.addComparator(e);
//		ordenacao.addComparator(f);
//		ordenacao.addComparator(g);
	
//		Collections.sort(resultAmbs, ordenacao);
		
		return resultAmbs;
	}
}
