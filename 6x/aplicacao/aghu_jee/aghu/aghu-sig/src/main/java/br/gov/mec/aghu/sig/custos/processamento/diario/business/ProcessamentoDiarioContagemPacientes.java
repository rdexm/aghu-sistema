package br.gov.mec.aghu.sig.custos.processamento.diario.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #27082 - A contagem diária de pacientes tem o objetivo de, diariamente, ir coletando os pacientes internados 
 * para depois coletar seus consumos e ao final do mês calcular o seu custo.
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoDiarioContagemPacientes.class)
public class ProcessamentoDiarioContagemPacientes extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719782L;

	@Override
	public String getTitulo() {
		return "Executa o processamento diario - contagem pacientes.";
	}

	@Override
	public String getNome() {
		return "processamentoDiarioContagemPacientesRN - contagemPacientes";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 26;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		//Passo 2
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_NOVOS_PACIENTES_INICIADA");

		//Passo 3
		List<AghAtendimentos> atendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscaInternacoesDentroIntervaloNaoCalculado(sigProcessamentoCusto);

		Short pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		
        Short pTipoGrupoContaSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		
		for (AghAtendimentos atendimento : atendimentos) {
			
			//Passo 4
			SigCalculoAtdPaciente calculoPaciente = new SigCalculoAtdPaciente();
			calculoPaciente.setProcessamentoCusto(sigProcessamentoCusto);
			calculoPaciente.setRapServidores(rapServidores);
			calculoPaciente.setCriadoEm(new Date());
			calculoPaciente.setAtendimento(atendimento);
			calculoPaciente.setIndPacientePediatrico(atendimento.getIndPacPediatrico());
			calculoPaciente.setInternacao(atendimento.getInternacao());
			calculoPaciente.setSituacaoCalculoPaciente((atendimento.getDthrFim() == null) ? DominioSituacaoCalculoPaciente.I: DominioSituacaoCalculoPaciente.IA);
			calculoPaciente.setPagador(this.getProcessamentoCustoUtils().getSigCalculoAtdProcedimentosDAO().obterPagador(calculoPaciente.getInternacao().getSeq()));

			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().persistir(calculoPaciente);
			this.commitProcessamentoCusto();

			//Passo 5
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_ATENDIMENTO_PACIENTE_INCLUIDO", atendimento.getSeq() + "/" + atendimento.getProntuario());
		}

		//Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_ATUALIZACAO_PACIENTES_ALTA");
		
		
		//Passo 7
		List<SigCalculoAtdPaciente> buscaAltasAtendimentoInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarAltasAtendimentoInternacao(sigProcessamentoCusto);
		
		for (SigCalculoAtdPaciente calculoPaciente : buscaAltasAtendimentoInternacao) {
			
			//Passo 8
			if (calculoPaciente.getSituacaoCalculoPaciente() == DominioSituacaoCalculoPaciente.I) {
				calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.IA);
				this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(calculoPaciente);
			}

			//Passo 9 ao 14
			this.inserirCidsProcedimentos(calculoPaciente, rapServidores, pConvenioSus, pTipoGrupoContaSus);
			
			this.commitProcessamentoCusto();
		}

		//Passo 15
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_FIM_CONTAGEM_PACIENTES");
	}
}
