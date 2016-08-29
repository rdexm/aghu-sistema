package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EncerramentoAutomaticoContaHospitalarON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EncerramentoAutomaticoContaHospitalarON.class);
	
	private static final long serialVersionUID = 4177094287716041813L;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ContaHospitalarON contaHospitalarON;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	public void agendarEncerramentoAutomaticoContaHospitalar(final Date date, final String cronn, final String nomeMicrocomputador, final RapServidores servidorLogado, final String nomeProcessoQuartz) {
		if (nomeProcessoQuartz == null) {
			throw new IllegalArgumentException("Nome do job não pode ser nulo.");
		}
		LOG.info("Quartz Task de Encerramento Automático Status: " + " [" + nomeProcessoQuartz + "]" + DateFormatUtil.formataTimeStamp(new Date()) + ";");
		
		AghJobDetail job = schedulerFacade.obterAghJobDetailPorNome(nomeProcessoQuartz);
		boolean finalizouSucesso = false;
		String stringException = "";		
		
		try {
	
			final Date dataFimVinculoServidor = new Date();
			LOG.info("Execução do encerramento automático " + DateUtil.obterDataFormatada(dataFimVinculoServidor, "dd/MM/yyyy HH:mm"));
			
			finalizouSucesso = contaHospitalarON.encerrarContasHospitalares(true, null, nomeMicrocomputador, dataFimVinculoServidor, job);
			
			LOG.info("Finalização da Execução do encerramento automático "+ DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
			
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			finalizouSucesso = false;
		}
		
		int size = 100 + stringException.length();
		
		StringBuffer mensagem = new StringBuffer(size);
		DominioSituacaoJobDetail situacao;
		mensagem.append("Quartz Task de Encerramento Automático Status - finalizou com: ");
		
		if (finalizouSucesso) {
			mensagem.append("SUCESSO");
			situacao = DominioSituacaoJobDetail.C;
		}
		else {
			mensagem.append("FALHA <br />");
			mensagem.append(stringException);
			situacao = DominioSituacaoJobDetail.F;
		}
	
		schedulerFacade.finalizarExecucao(job, situacao, mensagem.toString());
	}
}
