package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.AltasPorPeriodoVO;
import br.gov.mec.aghu.model.AinInternacao;

@Stateless
public class RelatorioAltasPorPeriodoRN extends BaseBusiness{

	private static final long serialVersionUID = -2482143732373857576L;

	private enum RelatorioAltasPorPeriodoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RELATORIO_VAZIO, MENSAGEM_RELATORIO_VAZIO_ALTAS_PELO_PERIODO
	}
	
	private static final Log LOG = LogFactory.getLog(RelatorioAltasPorPeriodoRN.class);
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AltasPorPeriodoVO> pesquisarAltasPorPeriodo(Date peridoInicial, Date periodoFinal) throws ApplicationBusinessException {
		List<AinInternacao> result = ainInternacaoDAO.pesquisarAltasNoPeriodo(peridoInicial, periodoFinal);
		try {
			List<AltasPorPeriodoVO> listaAltasPorPeriodoVO = new ArrayList<>();
			popularAltasPorPeriodoVO(result, listaAltasPorPeriodoVO);
			return listaAltasPorPeriodoVO;
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao gerar o Relatório de altas no periodo.");
			throw new ApplicationBusinessException(
					RelatorioAltasPorPeriodoRNExceptionCode.MENSAGEM_RELATORIO_VAZIO_ALTAS_PELO_PERIODO);
		}
	}

	private void popularAltasPorPeriodoVO(List<AinInternacao> result, List<AltasPorPeriodoVO> listaAltasPorPeriodoVO) throws ApplicationBusinessException {
		if (result.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioAltasPorPeriodoRNExceptionCode.MENSAGEM_RELATORIO_VAZIO);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (AinInternacao internacaoComAlta : result) {
			
			AltasPorPeriodoVO altaPorPeriodoVO = new AltasPorPeriodoVO(	internacaoComAlta.getAtendimento().getProntuario().toString(),
																		internacaoComAlta.getTipoAltaMedica().getCodigo(), //OBTIDO
																		internacaoComAlta.getPaciente().getNome(),
																		internacaoComAlta.getLeito().getLeitoID(),
																		internacaoComAlta.getEspecialidade().getSigla(),
																		"",// CRM
																		internacaoComAlta.getServidorDigita().getPessoaFisica().getNome(),
																		sdf.format(internacaoComAlta.getDthrInternacao()), 
																		sdf.format(internacaoComAlta.getDthrAltaMedica()),
																		internacaoComAlta.getDtSaidaPaciente(),
																		"",// senha
																		"",//codigoConvenio
																		"" //descricaoConvenio
																		);
			
			listaAltasPorPeriodoVO.add(altaPorPeriodoVO);
		}
	}
	
	
}
