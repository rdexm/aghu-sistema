package br.gov.mec.aghu.exames.business;


import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.ILaudoReport;
import br.gov.mec.aghu.exames.laudos.LaudoExterno;
import br.gov.mec.aghu.exames.laudos.LaudoSamis;
import br.gov.mec.aghu.exames.laudos.LaudoVisualizacao;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExamesId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LaudosExamesON extends BaseBusiness {

	private static final long serialVersionUID = 6627266690113752936L;
	
	private static final Log LOG = LogFactory.getLog(LaudosExamesON.class);
	
	private String caminhoLogo;
	
	private enum LaudosExamesONExceptionCode implements BusinessExceptionCode {
		 ERRO_AO_GERAR_PDF_LAUDO;
	 }

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	public ResultadoLaudoVO executaLaudo(
			List<IAelItemSolicitacaoExamesId> itemIds,
			DominioTipoImpressaoLaudo tipoLaudo)
			throws ApplicationBusinessException,
			BaseException {
		ExamesListaVO dadosLaudo = this.getMascaraExamesJasperReportON()
				.buscarDadosLaudo(itemIds);

		return this.executaLaudo(dadosLaudo, tipoLaudo);
	}

	public ResultadoLaudoVO executaLaudo(ExamesListaVO dadosLaudo,
			DominioTipoImpressaoLaudo tipoLaudo)
			throws ApplicationBusinessException,
			BaseException {

		// temporario
		// UtilSerialize.serializa(dadosLaudo);

		ByteArrayOutputStream baos = null;
		ILaudoReport laudo = null;
		try {
			if (tipoLaudo == null) {
				laudo = new LaudoVisualizacao();
			} else {
				if (DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO
						.equals(tipoLaudo)) {
					laudo = new LaudoExterno();
				} else if (DominioTipoImpressaoLaudo.LAUDO_SAMIS
						.equals(tipoLaudo)) {
					laudo = new LaudoSamis();
				}
			}
			// gera os laudos
			laudo.setCaminhoLogo(this.getCaminhoLogo());
			laudo.setExamesLista(dadosLaudo);
			laudo.executar();
			baos = new ByteArrayOutputStream();
			laudo.toPdf(baos);
			//laudo.toJrXml(new FileOutputStream("/home/toni/report.jrxml"));
		} catch (Exception e) {
			LOG.error("Erro na geração de laudo.", e);
			throw new ApplicationBusinessException(LaudosExamesONExceptionCode.ERRO_AO_GERAR_PDF_LAUDO, e);
		}
		ResultadoLaudoVO vo = new ResultadoLaudoVO();
		vo.setFontsAlteradas(laudo.getFontsAlteradas());
		vo.setOutputStreamLaudo(baos);
		
		return vo;
	}

	private MascaraExamesJasperReportON getMascaraExamesJasperReportON() {
		return new MascaraExamesJasperReportON();
	}
	
	
	public void setCaminhoLogo(String caminhoLogo){
		this.caminhoLogo = caminhoLogo;
	}
	
	public String getCaminhoLogo(){
		return this.caminhoLogo;
	}

}
