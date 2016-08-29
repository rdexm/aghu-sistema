package br.gov.mec.aghu.sig.custos.processamento.diario.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentoCirurgicoPacienteVO;
import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigObjetoCustoAnalise;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoDiarioContagemBusiness;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoCirurgiaVO;
import br.gov.mec.aghu.sig.custos.vo.EquipeCirurgiaVO;
import br.gov.mec.aghu.sig.custos.vo.MaterialCirurgiaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28062
 * 
 * Classe de processamento diário - Contagem de cirurgias do
 * paciente(atendimento) por unidade de internação, especialidade e equipe.
 * 
 * @author jgugel
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICProdCirurgicos extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = -4866342497225330580L;
	
	/**
	 * 
	 * Executa a contagem das cirurgias vinculadas ao atendimento passado por
	 * parametro
	 * 
	 * @param sigProcessamentoCusto
	 *            - Passado pelo processamento
	 * @param servidor
	 *            - Passado pelo processamento
	 * @param sigProcessamentoPassos
	 *            - Passado pelo processamento
	 * @param calculoAtendimento
	 *            - calculo atendimento
	 * @throws ApplicationBusinessException
	 *             - Exeção lançada caso ocorra algum erro durante a execução do
	 *             método
	 * @throws ApplicationBusinessException
	 *             - Exeção lançada caso o parametro não teja informações
	 */
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {
		
		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();
		// Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.CR);
		
		SigCategoriaRecurso categoriaRecurso1 = categoriasRecursos.get(1);
		SigCategoriaRecurso categoriaRecurso2 = categoriasRecursos.get(2);
		SigCategoriaRecurso categoriaRecurso3 = categoriasRecursos.get(3);
		
		//Passo 2
		List<ProcedimentoCirurgicoPacienteVO> listaCirurgias = this.getProcessamentoCustoUtils().getBlocoCirurgicoFacade()
				.buscarCirurgiasDoPacienteDuranteUmaInternacao(atendimento.getSeq(), sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());

		// Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_INICIO_CONTAGEM_PROCEDIMENTOS_CIRURGICOS");

		if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaCirurgias)){
			
			SigAtividades atividade = (SigAtividades) parametros.get(AghuParametrosEnum.P_AGHU_SIG_SEQ_REALIZAR_CIRURGIA_NO_PACIENTE);

			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			SigCalculoAtdConsumo consumoUnidadeInternacao = null, consumoEspecialidade = null, consumoEquipe = null;
			FccCentroCustos centroCusto = null;
			AghEspecialidades especialidade;
			RapServidores equipe;
			SigObjetoCustoVersoes ocv;
			FatProcedHospInternos phi;
			BigDecimal somaTempo;
			MbcCirurgias cirurgia = null;
			
			List<MaterialCirurgiaVO> listaMateriais = null;
			List<EquipamentoCirurgiaVO> listaEquipamentos = null;
			List<EquipeCirurgiaVO> listaEquipes = null;
			
			Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
			Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumos = new HashMap<String, SigCalculoDetalheConsumo>();
			Map<Integer, SigObjetoCustoVersoes> cacheObjetoCustoVersao = new HashMap<Integer, SigObjetoCustoVersoes>();
			
			// Passo 4
			for(ProcedimentoCirurgicoPacienteVO vo : listaCirurgias){

				//Se o procedimento cirurgico não possuir PHI_SEQ, deve buscar através do EPR_PCI_SEQ
				if(vo.getPhiSeq() == null){
					vo.setPhiSeq(this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().obterPhiPorPciSeq(vo.getPciSeq()));
				}
				
				//So deve carregar as permaneência quando trocar a cirurgia
				if(cirurgia == null || !cirurgia.getSeq().equals(vo.getCrgSeq())){
					
					cirurgia = this.getProcessamentoCustoUtils().getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(vo.getCrgSeq());
					
					centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());
					especialidade = this.getProcessamentoCustoUtils().getAghuFacade().obterAghEspecialidadesPorChavePrimaria(vo.getEspSeq());
					equipe =  this.getProcessamentoCustoUtils().getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(vo.getPucSerMatricula(), vo.getPucSerVinCodigo());
					
					// Passo 5, 6, 7 (as permanências fazem referência a cirurgia)
					somaTempo = calcularDiferencaMinutos(vo.getDataEntradaSala(), vo.getDataSaidaSala());
					permanenciaUnidadeInternacao = this.manterPermanencia(calculoAtdPaciente, DominioCalculoPermanencia.UI, somaTempo,  rapServidores,  centroCusto, null, null, sigProcessamentoCusto);
					permanenciaEspecialidade = this.manterPermanencia(calculoAtdPaciente, DominioCalculoPermanencia.SM, somaTempo,   rapServidores, null,  especialidade, null, sigProcessamentoCusto);
					permanenciaEquipe = this.manterPermanencia(calculoAtdPaciente, DominioCalculoPermanencia.EQ, somaTempo, rapServidores, null, null, equipe, sigProcessamentoCusto);
					
					// Passo 8
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_PERMANENCIA_ATUALIZADA_SUCESSO");
					
					//Os materiais, equipamentos e equipes também estão ligados a cirurgia
					
					// Passo - 11
					listaMateriais = this.getProcessamentoCustoUtils().getSigEquipamentoPatrimonioDAO().buscarMateriaisCirurgia(vo.getCrgSeq());
					
					// Passo - 13
					listaEquipamentos = this.getProcessamentoCustoUtils().getFaturamentoFacade().buscarEquipamentos(vo.getCrgSeq());
					
					// Passo - 15
					listaEquipes = this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().buscaEquipeCirurgia(vo.getCrgSeq());
				}
					
				phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(), cachePhis);
				ocv = this.buscarVersaoAtivaObjetoCusto(phi, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, cacheObjetoCustoVersao);

				// Passo 9,10
				consumoUnidadeInternacao = cadastrarConsumoAnalise(permanenciaUnidadeInternacao, cirurgia, phi, vo.getIndPrincipal(), sigProcessamentoCusto, centroCusto, ocv, rapServidores, sigProcessamentoPassos, categoriaConsumo);
				consumoEspecialidade = cadastrarConsumoAnalise(permanenciaEspecialidade, cirurgia, phi, vo.getIndPrincipal(), sigProcessamentoCusto, centroCusto, ocv, rapServidores, sigProcessamentoPassos, categoriaConsumo);
				consumoEquipe = cadastrarConsumoAnalise(permanenciaEquipe, cirurgia, phi, vo.getIndPrincipal(), sigProcessamentoCusto, centroCusto, ocv, rapServidores, sigProcessamentoPassos, categoriaConsumo);
		
				// Passo - 11,12
				this.cadastrarMateriaisCirurgia(listaMateriais, categoriaRecurso2, vo.getCrgSeq(), centroCusto, rapServidores, sigProcessamentoPassos, consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe,  atividade, sigProcessamentoCusto, cacheDetalheConsumos);
		
				// Passo - 13,14
				this.cadastrarEquipamentosCirurgia(listaEquipamentos, categoriaRecurso3, vo.getCrgSeq(), centroCusto, rapServidores, sigProcessamentoPassos, consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe,  atividade, sigProcessamentoCusto, cacheDetalheConsumos);
		
				// Passo - 15,16
				this.cadastrarEquipeCirurgia(listaEquipes, categoriaRecurso1, vo, rapServidores, sigProcessamentoPassos, consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe, atividade, sigProcessamentoCusto, cacheDetalheConsumos);	
				
				this.commitProcessamentoCusto();
			}
		}
	}

	private SigCalculoAtdPermanencia manterPermanencia(SigCalculoAtdPaciente calculoAtendimento, DominioCalculoPermanencia tipo, BigDecimal tempo, RapServidores servidor, FccCentroCustos centroCusto, AghEspecialidades especialidade, RapServidores equipe, SigProcessamentoCusto sigProcessamentoCusto ){
		
		SigCalculoAtdPermanencia permanencia = null;
		
		//Passo 5
		if(centroCusto != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(calculoAtendimento.getAtendimento(), centroCusto, sigProcessamentoCusto);
		}
		else if(especialidade != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(calculoAtendimento.getAtendimento(), especialidade, sigProcessamentoCusto);
		}
		else if(equipe != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(calculoAtendimento.getAtendimento(), equipe, sigProcessamentoCusto);
		}

		//Passo 6
		if (permanencia == null) {
			permanencia = new SigCalculoAtdPermanencia();
			permanencia.setCriadoEm(new Date());
			permanencia.setCalculoAtdPaciente(calculoAtendimento);
			permanencia.setRapServidores(servidor);
			permanencia.setTipo(tipo);
			permanencia.setTempo(tempo);
			permanencia.setCentroCustos(centroCusto);
			permanencia.setEspecialidade(especialidade);
			permanencia.setResponsavel(equipe);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		} 
		//Passo 7
		else {
			permanencia.setTempo(permanencia.getTempo().add(tempo));
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().atualizar(permanencia);
		}
		
		return permanencia;
	}

	private SigCalculoAtdConsumo cadastrarConsumoAnalise(SigCalculoAtdPermanencia permanencia, MbcCirurgias cirurgia,  FatProcedHospInternos phi, Boolean indPrincipal, SigProcessamentoCusto sigProcessamentoCusto,
			FccCentroCustos centroCusto, SigObjetoCustoVersoes ocv,
			RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {

		//Passo 9
		SigCalculoAtdConsumo consumo = new SigCalculoAtdConsumo();	
		
		if(ocv != null){
			consumo.setSigObjetoCustoVersoes(ocv);
		}
		else{
			consumo.setProcedHospInterno(phi);
		}
		
		consumo.setCriadoEm(new Date());
		consumo.setCirurgia(cirurgia);
		consumo.setCentroCustos(centroCusto);
		consumo.setCalculoAtividadePermanencia(permanencia);
		consumo.setQtde(BigDecimal.ONE);
		consumo.setRapServidores(sigProcessamentoCusto.getRapServidores());
		consumo.setCategoriaConsumo(categoriaConsumo);
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		
		if(ocv != null){
			// Passo - 10
			SigObjetoCustoAnalise analise = new SigObjetoCustoAnalise();
			analise.setCriadoEm(new Date());
			analise.setSigObjetoCustoVersoes(ocv);
			analise.setCalculoAtividadeConsumo(consumo);
			analise.setPrincipal(indPrincipal);
			analise.setRapServidores(sigProcessamentoCusto.getRapServidores());
			this.getProcessamentoCustoUtils().getSigObjetoCustoAnaliseDAO().persistir(analise);
		}

		return consumo;
	}

	private SigObjetoCustoVersoes buscarVersaoAtivaObjetoCusto(FatProcedHospInternos phi, SigProcessamentoCusto sigProcessamentoCusto, RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, Map<Integer, SigObjetoCustoVersoes> cacheObjetoCustoVersao) throws ApplicationBusinessException {
		
		if(!cacheObjetoCustoVersao.containsKey(phi.getSeq())){
			SigObjetoCustoVersoes versaoAtiva = null;
			
			List<SigObjetoCustoVersoes> listVersaoAtiva = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().buscarListaObjetoCustoAtivoPeloPHI(phi);
			if(ProcessamentoCustoUtils.verificarListaNaoVazia(listVersaoAtiva)){
				if (listVersaoAtiva != null && listVersaoAtiva.size() > 1) {
					String nomesOCConcatenados = "";
					for (SigObjetoCustoVersoes sigObjetoCustoVersoes : listVersaoAtiva) {
						nomesOCConcatenados = sigObjetoCustoVersoes.getSigObjetoCustos().getNome() + ", ";
					}
					this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_ERRO_DE_CC_OU_PHI_NO_OC", nomesOCConcatenados);
					throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_DE_CC_OU_PHI_NO_OC, nomesOCConcatenados);
				}
		
				versaoAtiva = listVersaoAtiva.get(0);
				if (versaoAtiva.getSigObjetoCustoCctsPrincipal().getSeq() != null) {
					this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor,sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_ERRO_DE_CC_OU_PHI_NO_OC", versaoAtiva.getSigObjetoCustos().getNome());
					throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_DE_CC_OU_PHI_NO_OC, versaoAtiva.getSigObjetoCustos().getNome());
				}
			}
			
			cacheObjetoCustoVersao.put(phi.getSeq(), versaoAtiva);
		}
		return cacheObjetoCustoVersao.get(phi.getSeq());
	}

	private void cadastrarMateriaisCirurgia(List<MaterialCirurgiaVO> listaMateriais, SigCategoriaRecurso categoriaRecurso, Integer crgSeq, FccCentroCustos centroCusto,
			RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdConsumo consumoUnidadeInternacao, SigCalculoAtdConsumo consumoEspecialidade, SigCalculoAtdConsumo consumoEquipe, SigAtividades atividade, SigProcessamentoCusto sigProcessamentoCusto, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumos)
			throws ApplicationBusinessException {
		
		FatProcedHospInternos phi;
		BigDecimal quantidade;
		ScoMaterial material;
		//Passo - 11
		for (MaterialCirurgiaVO vo : listaMateriais) {
			
			if(vo.getPhi() != null){
				phi = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterProcedimentoHospitalarInterno(vo.getPhi());
			}
			else{
				phi = null;
			}
			material =  this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo());
			quantidade = new BigDecimal(vo.getSoma());
			this.manterDetalheConsumo(consumoUnidadeInternacao, quantidade, phi, material, centroCusto, atividade, categoriaRecurso, servidor, null, null, cacheDetalheConsumos);
			this.manterDetalheConsumo(consumoEspecialidade, quantidade, phi, material, centroCusto, atividade, categoriaRecurso, servidor, null, null, cacheDetalheConsumos);
			this.manterDetalheConsumo(consumoEquipe, quantidade, phi, material, centroCusto, atividade, categoriaRecurso, servidor, null, null, cacheDetalheConsumos);
			this.commitProcessamentoCusto();
		}
		
		// Passo - 12
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_MATERIAIS_UTILIZADO_CIRURGIA", crgSeq);
	}

	private void cadastrarEquipamentosCirurgia(List<EquipamentoCirurgiaVO> listaEquipamentos, SigCategoriaRecurso categoriaRecurso, Integer crgSeq, FccCentroCustos centroCusto,
			RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdConsumo consumoUnidadeInternacao, SigCalculoAtdConsumo consumoEspecialidade, SigCalculoAtdConsumo consumoEquipe, SigAtividades atividade, SigProcessamentoCusto sigProcessamentoCusto, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumos)
			throws ApplicationBusinessException {
		
		FatProcedHospInternos phi;
		BigDecimal quantidade;
		// Passo - 13
		for (EquipamentoCirurgiaVO vo : listaEquipamentos) {
			
			phi = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterProcedimentoHospitalarInterno(vo.getSeqPhi());
			quantidade = new BigDecimal(vo.getSumQtde().doubleValue());
			
			this.manterDetalheConsumo(consumoUnidadeInternacao, quantidade, phi, null, centroCusto, atividade, categoriaRecurso,  servidor, null, null, cacheDetalheConsumos);
			this.manterDetalheConsumo(consumoEspecialidade, quantidade, phi, null, centroCusto, atividade, categoriaRecurso,  servidor, null, null, cacheDetalheConsumos);
			this.manterDetalheConsumo(consumoEquipe, quantidade, phi, null, centroCusto, atividade, categoriaRecurso,  servidor, null, null, cacheDetalheConsumos);
			this.commitProcessamentoCusto();
		}

		// Passo - 14
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_EQUIPAMENTOS_UTILIZADO_CIRURGIA", crgSeq);
	}

	private void cadastrarEquipeCirurgia(List<EquipeCirurgiaVO> listaEquipes, SigCategoriaRecurso categoriaRecurso,  ProcedimentoCirurgicoPacienteVO procedimentoCirurgicoVO,
			RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdConsumo consumoUnidadeInternacao, SigCalculoAtdConsumo consumoEspecialidade, SigCalculoAtdConsumo consumoEquipe, SigAtividades atividade,  SigProcessamentoCusto sigProcessamentoCusto, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumos)
			throws ApplicationBusinessException {
		
		BigDecimal quantidade;
		FccCentroCustos centroCusto;
		SigGrupoOcupacoes grupoOcupacao=null; 
		RapOcupacaoCargo ocupacaoCargo=null;
		
		// Passo 15
		for (EquipeCirurgiaVO vo : listaEquipes) {
			
			centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());
			quantidade = BigDecimal.ZERO;
			
			if (vo.getTipo().equals("A")) {
				quantidade = this.calcularDiferencaMinutos(procedimentoCirurgicoVO.getDataInicioAnestesia(),procedimentoCirurgicoVO.getDataFimAnestesia());
			} else if (vo.getTipo().equals("M")) {
				quantidade = this.calcularDiferencaMinutos(procedimentoCirurgicoVO.getDataInicioCirurgia(),procedimentoCirurgicoVO.getDataFimCirurgia());
			} else if (vo.getTipo().equals("O")) {
				quantidade = this.calcularDiferencaMinutos(procedimentoCirurgicoVO.getDataEntradaSala(), procedimentoCirurgicoVO.getDataSaidaSala());
			}
			
			if(vo.getSeqGrupoOcupacao() != null){
				grupoOcupacao = this.getProcessamentoCustoUtils().getSigGrupoOcupacoesDAO().obterPorChavePrimaria(vo.getSeqGrupoOcupacao());
				ocupacaoCargo = null;
			} else{
				ocupacaoCargo = this.getProcessamentoCustoUtils().getCadastrosBasicosFacade().obterOcupacaoCargo(new RapOcupacoesCargoId(vo.getOcaCarCodigo(), vo.getOcaCodigo()));
				grupoOcupacao = null;
			}
					
			this.manterDetalheConsumo(consumoUnidadeInternacao, quantidade, null, null, centroCusto, atividade, categoriaRecurso, servidor, grupoOcupacao, ocupacaoCargo, cacheDetalheConsumos);
			this.manterDetalheConsumo(consumoEspecialidade, quantidade, null, null, centroCusto, atividade, categoriaRecurso, servidor, grupoOcupacao, ocupacaoCargo, cacheDetalheConsumos);
			this.manterDetalheConsumo(consumoEquipe, quantidade, null, null, centroCusto, atividade, categoriaRecurso, servidor, grupoOcupacao, ocupacaoCargo, cacheDetalheConsumos);
			this.commitProcessamentoCusto();
		}

		// Passo - 16
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_PROFISSIONAIS_UTILIZADO_CIRURGIA", procedimentoCirurgicoVO.getCrgSeq());
	}

	private void manterDetalheConsumo(SigCalculoAtdConsumo consumo, BigDecimal quantidade, FatProcedHospInternos phi, ScoMaterial material, FccCentroCustos centroCusto,  SigAtividades atividade, SigCategoriaRecurso categoriaRecurso, RapServidores servidor, SigGrupoOcupacoes grupoOcupacao, RapOcupacaoCargo ocupacaoCargo, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumos) {
		
		String chave = consumo.getSeq() +"-"+(phi != null ? phi.getSeq() : null)+"-"+(grupoOcupacao != null ? grupoOcupacao.getSeq() : null)+"-"+(ocupacaoCargo != null ? ocupacaoCargo.getCodigo() : null);
		if(!cacheDetalheConsumos.containsKey(chave)){
			if(phi!= null){
				cacheDetalheConsumos.put(chave, this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi, grupoOcupacao, ocupacaoCargo));
			}
		}

		SigCalculoDetalheConsumo detalheConsumo = cacheDetalheConsumos.get(chave) ; 
		if(detalheConsumo == null){
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCriadoEm(new Date());
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setAtividade(atividade);
			detalheConsumo.setQtdePrevisto(quantidade);
			detalheConsumo.setQtdeDebitado(quantidade);
			detalheConsumo.setQtdeConsumido(quantidade);
			detalheConsumo.setRapServidores(servidor);
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			detalheConsumo.setCentroCustos(centroCusto);
			detalheConsumo.setScoMaterial(material);
			detalheConsumo.setSigGrupoOcupacoes(grupoOcupacao);
			detalheConsumo.setRapOcupacaoCargo(ocupacaoCargo);
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);
			cacheDetalheConsumos.put(chave, detalheConsumo);
		}
		else{
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(quantidade));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(quantidade));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(quantidade));
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}
	}
	
	private BigDecimal calcularDiferencaMinutos(Date dataInicio, Date dataFim) {
		if(dataInicio != null & dataFim != null){
			long differenceMilliSeconds = dataFim.getTime() - dataInicio.getTime();
			Double calculoTempo = (double) (differenceMilliSeconds / 1000 / 60);
			return new BigDecimal(calculoTempo.toString());
		}
		return BigDecimal.ZERO;
	}
}
