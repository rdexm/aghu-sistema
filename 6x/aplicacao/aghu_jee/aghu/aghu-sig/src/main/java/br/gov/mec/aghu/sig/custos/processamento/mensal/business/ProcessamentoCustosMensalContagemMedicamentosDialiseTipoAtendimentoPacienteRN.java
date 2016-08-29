package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.EceKitComponente;
import br.gov.mec.aghu.model.EceViaDoKit;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.SigAtendimentoDialiseVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32220 - Contagem de medicamentos de diálise por tipo de atendimento do paciente
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN.class)
public class ProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN extends ProcessamentoMensalBusiness {

	protected static final long serialVersionUID = -8364666994674519645L;
	@Override
	public String getTitulo() {
		return "Contagem de medicamentos de diálise por tipo de atendimento do paciente";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 31;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {		
		
		//Passo 1
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.DM, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		SigCategoriaRecurso categoriaRecurso = this.getProcessamentoCustoUtils().getSigCategoriaRecursoDAO().obterPorChavePrimaria(2);
		
		//Passo 2
		
		AghParametros parametroDialise =  this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_SIG_SEQ_DISPENSACAO_DIALISE);
		SigObjetoCustoVersoes objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterSigObjetoCustoVersaoAtivaPorParametro(parametroDialise.getVlrNumerico());
		if (objetoCustoVersao == null) {
			//FE02
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_OBJETO_CUSTO_PARAMETRIZADO_NAO_ENCONTRADO, "P_AGHU_SIG_SEQ_DISPENSACAO_DIALISE");
		}	
		
		//Passo 3
		List<SigAtendimentoDialiseVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarMedicamentosPrescricaoDialise(sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista) ) {
			
			// Passo 04
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "LISTA_DIALISE_OK", sigProcessamentoCusto.getCompetenciaMesAno());
			
			this.processarMedicamentosDialise(lista, objetoCustoVersao, categoriaConsumo, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		}
		//FE03
		else{
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "DIALISE_FE01", sigProcessamentoCusto.getCompetenciaMesAno());
		}
	}

	protected void processarMedicamentosDialise(List<SigAtendimentoDialiseVO> lista, SigObjetoCustoVersoes objetoCustoVersao, SigCategoriaConsumos categoriaConsumo, SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		
		Short pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		
        Short pTipoGrupoContaSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		
		SigCalculoAtdPaciente calculoPaciente = null;
		AinMovimentosInternacao ultimoMovimentoInternacao = null;
		List<AghAtendimentos> atendimentos;
		AghAtendimentos atendimento;
		MptTratamentoTerapeutico tratamentoTerapeutico;
		FccCentroCustos centroCusto, centroCustoUtilizadoPermanencia;
		DominioCalculoPermanencia tipoUnidadeFuncional;
		FatProcedHospInternos phi;		
		ScoMaterial material;
		SigCalculoAtdPermanencia permanenciaUnidadeFuncional, permanenciaEspecialidade, permanenciaEquipe;
		SigCalculoAtdConsumo consumoUnidadeFuncional, consumoEspecialidade, consumoEquipe;
		
		//Passo 05
		for (SigAtendimentoDialiseVO vo : lista) {

			tratamentoTerapeutico = this.getProcessamentoCustoUtils().getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(vo.getTrpSeq());	
			
			//Passo 06
			atendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscarAtendimentos(vo.getPacCodigo(), vo.getDtPrevExecucao());

			if (ProcessamentoCustoUtils.verificarListaNaoVazia(atendimentos)) {
				atendimento = atendimentos.get(0);
				
				//Passo 7
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(atendimento.getInternacao().getSeq(), vo.getDtDispensacao());
				if(ultimoMovimentoInternacao == null){
					continue;
				}
				
				centroCustoUtilizadoPermanencia = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
				tipoUnidadeFuncional = DominioCalculoPermanencia.UI;
			}
			else{
				atendimento = null;
				ultimoMovimentoInternacao = null;
				centroCustoUtilizadoPermanencia = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorUnidadeFuncional(tratamentoTerapeutico.getUnfSeq());
				tipoUnidadeFuncional = DominioCalculoPermanencia.UA;
			}
			
			//Passo 8, 9, 10
			calculoPaciente = this.manterCalculoPaciente(vo.getAtdPaciente(), atendimento, tratamentoTerapeutico, sigProcessamentoCusto, rapServidores, pConvenioSus, pTipoGrupoContaSus);

			//Passo 11
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto,rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ATENDIMENTO_PACIENTE_INCLUIDO_CALCULO", vo.getPacCodigo().toString(), vo.getAtdPaciente().toString());
			
			//Passo 12
			AghEspecialidades especialidade = tratamentoTerapeutico.getEspecialidade();
			RapServidores equipe = tratamentoTerapeutico.getServidorResponsavel();

			//Passo 13
			permanenciaUnidadeFuncional =  this.manterPermanencia(calculoPaciente, tipoUnidadeFuncional, rapServidores, centroCustoUtilizadoPermanencia, null, null, sigProcessamentoCusto);

			//Passo 14
			permanenciaEspecialidade = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.SM, rapServidores, null, especialidade, null, sigProcessamentoCusto);

			//Passo 15
			permanenciaEquipe = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.EQ, rapServidores, null, null, equipe, sigProcessamentoCusto);

			//Passo 16
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "TEMPO_PERMANENCIA_ATUALIZADO", centroCustoUtilizadoPermanencia.getDescricao(), especialidade.getNomeEspecialidade(), equipe.getPessoaFisica().getNome());

			centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCentroCusto());
			
			//Passo 17, 18
			consumoUnidadeFuncional = this.manterCalculoConsumo(permanenciaUnidadeFuncional, vo.getQuantidadeDispensada(), objetoCustoVersao, centroCusto, categoriaConsumo, rapServidores);
			consumoEspecialidade = this.manterCalculoConsumo(permanenciaEspecialidade, vo.getQuantidadeDispensada(), objetoCustoVersao, centroCusto, categoriaConsumo, rapServidores);
			consumoEquipe = this.manterCalculoConsumo(permanenciaEquipe, vo.getQuantidadeDispensada(), objetoCustoVersao, centroCusto, categoriaConsumo, rapServidores);
			
			//Passo 19 
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ATENDIMENTO_ASSOCIADO_CENTRO_CUSTO", vo.getAtdPaciente(), objetoCustoVersao.getSeq(), centroCusto.getDescricao(), especialidade.getNomeEspecialidade(), equipe.getPessoaFisica().getNome());
			
			// Passo 21
			BigDecimal quantidadeKits = BigDecimal.valueOf(this.calcularNumeroVezesAprazamento(vo.getTfqSeq(), vo.getFrequencia()) * 1);
			
			phi = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterFatProcedHospInternosPorChavePrimaria(vo.getPhiSeq());
			material = this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMedMat());

			//Passo 22, 23, 24
			this.manterDetalheConsumo(consumoUnidadeFuncional, vo.getQuantidadeSolicitada(), vo.getQuantidadeDispensada(), phi, material, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
			this.manterDetalheConsumo(consumoEspecialidade, vo.getQuantidadeSolicitada(), vo.getQuantidadeDispensada(), phi, material, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
			this.manterDetalheConsumo(consumoEquipe, vo.getQuantidadeSolicitada(), vo.getQuantidadeDispensada(), phi, material, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

			//Passo 26, 27, 28, 29, 30, 31
			this.processarKitsMedicamentos(vo, quantidadeKits, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, consumoUnidadeFuncional, consumoEspecialidade, consumoEquipe);
		
			this.commitProcessamentoCusto();
		}
	}
	
	protected void processarKitsMedicamentos(SigAtendimentoDialiseVO vo, BigDecimal quantidadeKits, SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdConsumo consumoUnidadeFuncional, SigCalculoAtdConsumo consumoEspecialidade, SigCalculoAtdConsumo consumoEquipe) throws ApplicationBusinessException {

		//Passo 26
		List<EceViaDoKit> kits = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscaKitMedicamento(vo.getPhiSeq(), vo.getVadSigla(), vo.getIndPacPediatrico() ? "S" : "N");

		if (ProcessamentoCustoUtils.verificarListaNaoVazia(kits)) {
			
			FatProcedHospInternos phi;
			ScoMaterial material;
			BigDecimal quantidadePrevisto, quantidadeDispensada;
			
			for (EceViaDoKit kit : kits) {	
				
				for (EceKitComponente componente : kit.getEceKitComponentees()) {
					
					phi = componente.getFatProcedHospInternos();
					material = componente.getFatProcedHospInternos().getMaterial();
					
					if (componente.getTipo().equals("U") || componente.getTipo().equals("D")) {
						quantidadePrevisto = quantidadeKits.multiply(BigDecimal.valueOf(componente.getQuantidade()));
						quantidadeDispensada = vo.getQuantidadeDispensada();
					}
					else{
						quantidadePrevisto = BigDecimal.ZERO;
						quantidadeDispensada = BigDecimal.ZERO;
					}
					
					//Passo 27, 28, 29
					this.manterDetalheConsumo(consumoUnidadeFuncional, quantidadePrevisto, quantidadeDispensada, phi, material, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
					this.manterDetalheConsumo(consumoEspecialidade, quantidadePrevisto, quantidadeDispensada, phi, material, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
					this.manterDetalheConsumo(consumoEquipe, quantidadePrevisto, quantidadeDispensada, phi, material, categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
				}
			}
		}
	}
	
	private SigCalculoAtdPaciente manterCalculoPaciente(Integer atdSeq, AghAtendimentos atendimento, MptTratamentoTerapeutico tratamentoTerapeutico, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, Short pConvenioSus, Short pTipoGrupoContaSus){
		
		//Passo 8
		List<SigCalculoAtdPaciente> listaCalculoPacientes = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarCalculoAtendimentoPaciente(atdSeq, sigProcessamentoCusto, atendimento != null ? atendimento.getInternacao() : null);
		
		//Passo 9
		SigCalculoAtdPaciente calculoPaciente;
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoPacientes)) {
			calculoPaciente = listaCalculoPacientes.get(0);
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(calculoPaciente);	
		} 
		//Passo 10
		else {
			calculoPaciente = new SigCalculoAtdPaciente();
			calculoPaciente.setCriadoEm(new Date());
			calculoPaciente.setProcessamentoCusto(sigProcessamentoCusto);

			if (atendimento != null) {
				calculoPaciente.setInternacao(atendimento.getInternacao());

				if (atendimento.getDthrFim() == null) {
					calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.I);
				} else if (atendimento.getDthrFim() != null) {
					calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.IA);
				}
			} else {
				calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.A);
			}
			
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			calculoPaciente.setAtendimento(this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq));
			calculoPaciente.setRapServidores(rapServidores);
			calculoPaciente.setIndPacientePediatrico(calculoPaciente.getAtendimento().getIndPacPediatrico());
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().persistir(calculoPaciente);
			this.inserirCidsProcedimentos(calculoPaciente, rapServidores, pConvenioSus, pTipoGrupoContaSus);
		}
		return calculoPaciente;
	}
	
	private SigCalculoAtdPermanencia manterPermanencia(SigCalculoAtdPaciente calculoPaciente, DominioCalculoPermanencia tipo, RapServidores servidor,  FccCentroCustos centroCusto, AghEspecialidades especialidade, RapServidores equipe, SigProcessamentoCusto sigProcessamentoCusto ){
		
		SigCalculoAtdPermanencia permanencia = null;
		
		if(centroCusto != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(calculoPaciente.getAtendimento(), centroCusto, sigProcessamentoCusto);
		}
		else if(especialidade != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(calculoPaciente.getAtendimento(), especialidade, sigProcessamentoCusto);
		}
		else if(equipe != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(calculoPaciente.getAtendimento(), equipe, sigProcessamentoCusto);
		}

		
		if (permanencia == null) {
			permanencia = new SigCalculoAtdPermanencia();
			permanencia.setCriadoEm(new Date());
			permanencia.setCalculoAtdPaciente(calculoPaciente);
			permanencia.setRapServidores(servidor);
			permanencia.setTipo(tipo);
			permanencia.setTempo(new BigDecimal(1440));
			permanencia.setCentroCustos(centroCusto);
			permanencia.setEspecialidade(especialidade);
			permanencia.setResponsavel(equipe);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		}
		
		return permanencia;
	}	
	
	private SigCalculoAtdConsumo manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, BigDecimal quantidadeCuidados, SigObjetoCustoVersoes objetoCustoVersao, FccCentroCustos centroCusto, SigCategoriaConsumos categoriaConsumo, RapServidores rapServidores){
		
		SigCalculoAtdConsumo  consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumoPorPermanenciaEVersao(permanencia.getSeq(), objetoCustoVersao.getSeq());
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setQtde( quantidadeCuidados);
			consumo.setRapServidores(rapServidores);
			consumo.setCriadoEm(new Date());
			consumo.setCentroCustos(centroCusto);
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		} else {
			consumo.setQtde(consumo.getQtde().add(quantidadeCuidados));
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		return consumo;
	}
	
	private void manterDetalheConsumo(SigCalculoAtdConsumo consumo, BigDecimal quantidadeSolicitada, BigDecimal quantidadeDispensada, FatProcedHospInternos phi, ScoMaterial material, SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		
		SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi);

		if (detalheConsumo == null) {
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setScoMaterial(material);
			detalheConsumo.setQtdePrevisto(quantidadeSolicitada);
			detalheConsumo.setQtdeDebitado(quantidadeDispensada);
			detalheConsumo.setQtdeConsumido(quantidadeDispensada);
			detalheConsumo.setRapServidores(rapServidores);
			detalheConsumo.setCriadoEm(new Date());
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);
		} else {
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(quantidadeSolicitada));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(quantidadeDispensada));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(quantidadeDispensada));
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}
	}	
}
