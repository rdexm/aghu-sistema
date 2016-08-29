package br.gov.mec.aghu.internacao.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.internacao.vo.PacienteIdadeCIDVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmCidAtendimento;

@Stateless
public class RelatorioIdadeCIDRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3927032574841724525L;

	private enum RelatorioIdadeCIDRNExceptionCode implements BusinessExceptionCode {
	MENSAGEM_RELATORIO_VAZIO
	
	}
	
	private static final Log LOG = LogFactory.getLog(RelatorioIdadeCIDRN.class);
	
	
	@Inject
	private AghCidDAO aghCidDAO;
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<PacienteIdadeCIDVO> pesquisarPacientesPorIdadeECID(Integer idadeInicial, Integer idadeFinal, AghCid cid) throws ApplicationBusinessException {
		
		Date dataNascimentoInicial = new Date();
		Date dataNascimentoFinal = new Date();
		
		dataNascimentoInicial = validaDataInicial(idadeInicial);
		dataNascimentoFinal = validaDataInicial(idadeFinal);
		List<MpmCidAtendimento> result = aghCidDAO.pesquisarPacientesPorIdadeECID(dataNascimentoInicial, dataNascimentoFinal,cid);
			List<PacienteIdadeCIDVO> listaPacienteIdadeCIDVO = new ArrayList<>();
			try {
				populaListaPacienteCIDVO(result, listaPacienteIdadeCIDVO);				
			} catch (ApplicationBusinessException e) {
				LOG.error("Exceção ApplicationBusinessException capturada.");
				throw new ApplicationBusinessException(
						RelatorioIdadeCIDRNExceptionCode.MENSAGEM_RELATORIO_VAZIO);
				}
		
		return listaPacienteIdadeCIDVO;
		
	}

	private void populaListaPacienteCIDVO(List<MpmCidAtendimento> result,
			List<PacienteIdadeCIDVO> listaPacienteIdadeCIDVO) throws ApplicationBusinessException {
		if (result.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioIdadeCIDRNExceptionCode.MENSAGEM_RELATORIO_VAZIO);
		}
		
		for (MpmCidAtendimento cidAtendimento : result) {
			
			PacienteIdadeCIDVO pacienteIdadeCIDVO = new PacienteIdadeCIDVO(	cidAtendimento.getAtendimento().getPaciente().getNome(), 
																			cidAtendimento.getAtendimento().getPaciente().getNomeMae(), 
																			cidAtendimento.getAtendimento().getPaciente().getDtNascimento(), 
																			cidAtendimento.getAtendimento().getPaciente().getSexo().toString(), 
																			DateUtil.getIdade(cidAtendimento.getAtendimento().getPaciente().getDtNascimento()), 
																			cidAtendimento.getAtendimento().getProntuario(), 
																			cidAtendimento.getCid().getCodigo().toString(), 
																			cidAtendimento.getCid().getDescricao().toString()
																		);
			listaPacienteIdadeCIDVO.add(pacienteIdadeCIDVO);
		}
	}

	private Date validaDataInicial(Integer idadeInicial) {
		Date dataNascimentoInicial;
		if (idadeInicial != null) {
			dataNascimentoInicial = obterDataApartirDaIdade(idadeInicial);	
		} else {
			dataNascimentoInicial = null;
		}
		return dataNascimentoInicial;
	}
	
	@SuppressWarnings("deprecation")
	private Date obterDataApartirDaIdade(int idade) {
		if (idade < 0) {
			return null;
		}
		
		Calendar cal = Calendar.getInstance();
		DateUtil.zeraHorario(cal);
		Date dataAtual = cal.getTime();
		
		dataAtual = DateUtil.adicionaMeses(dataAtual, -(idade*12));
		
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			return formato.parse(formato.format(dataAtual));
		} catch (ParseException e) {
			logError(e);
			return null;
		}
	}
	
}
