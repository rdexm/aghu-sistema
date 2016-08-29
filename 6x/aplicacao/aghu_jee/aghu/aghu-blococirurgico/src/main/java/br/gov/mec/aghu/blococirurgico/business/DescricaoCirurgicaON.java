package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de negócio para Descrição Cirúrgica.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class DescricaoCirurgicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DescricaoCirurgicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 5314248734524900457L;
	
	/**
	 * Calcula a idade por extenso de um paciente a partir
	 * da data de nascimento.
	 * 
	 * ORADB: MPMC_IDA_ANO_MES_DIA
	 * 
	 * @param dtNascimentoPaciente
	 * @return
	 */
	public String obterIdadePorDataNascimento(Date dtNascimentoPaciente) {
		Calendar calAtual = Calendar.getInstance();

		Date dataAtualTruncada = DateUtil.truncaData(calAtual.getTime());
		Date dataNascimentoTruncada = DateUtil.truncaData(dtNascimentoPaciente);
		
		Calendar calDtUltimoAniversario = Calendar.getInstance();
		calDtUltimoAniversario.setTime(dataNascimentoTruncada);		
		calDtUltimoAniversario.set(Calendar.YEAR, calAtual.get(Calendar.YEAR));		
		
		// Caso o paciente ainda não tenha feito aniversário no ano considerando a data atual
		if (calDtUltimoAniversario.getTime().after(dataAtualTruncada)) {
			calDtUltimoAniversario.set(Calendar.YEAR, calDtUltimoAniversario.get(Calendar.YEAR) - 1);
		}

		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(dataNascimentoTruncada, dataAtualTruncada);
		Integer meses = DateUtil.obterQtdMesesEntreDuasDatas(calDtUltimoAniversario.getTime(), dataAtualTruncada);
		Integer dias = DateUtil.obterQtdDiasEntreDuasDatas(calDtUltimoAniversario.getTime(), dataAtualTruncada);
		
		StringBuilder strIdade = new StringBuilder();
		
		if (anos == 0 && meses == 0) {
			concatenarIdadeDias(strIdade, dias);
			
		} else if (anos == 0) {
			Calendar calDtNascimentoMesAtual = Calendar.getInstance();
			calDtNascimentoMesAtual.setTime(dataNascimentoTruncada);		
			calDtNascimentoMesAtual.set(Calendar.MONTH, calAtual.get(Calendar.MONTH));
			
			dias = DateUtil.obterQtdDiasEntreDuasDatas(calDtNascimentoMesAtual.getTime(), dataAtualTruncada);
			
			concatenarIdadeMeses(strIdade, meses);
			concatenarIdadeDias(strIdade, dias);
			
		} else if (anos >= 1) {
			if (anos > 1) {
				strIdade.append(anos).append(" anos ");
			} else if (anos == 1) {
				strIdade.append(anos).append(" ano ");
			}
			concatenarIdadeMeses(strIdade, meses);
		}

		return strIdade.toString();
	}
	
	private void concatenarIdadeDias(StringBuilder strIdade, Integer dias) {
		if (dias > 1) {
			strIdade.append(dias).append(" dias");
		} else if (dias == 1) {
			strIdade.append(dias).append(" dia");
		}
	}
	
	private void concatenarIdadeMeses(StringBuilder strIdade, Integer meses) {
		if (meses > 1) {
			strIdade.append(meses).append(" meses ");
		} else if (meses == 1) {
			strIdade.append(meses).append(" mês ");
		}
	}

}
