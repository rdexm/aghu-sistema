package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ConsumoPacienteConsumidoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28219 - ALOCAR CUSTO DE OBJETOS DE CUSTO NOS PACIENTES
 * Essa classe tem por objetivo calcular o custo do paciente com base no custo médio calculado para os objetos de custo consumidos por ele, 
 * no mês de competência do processamento.
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalAlocarOCPacienteRN.class)
public class ProcessamentoCustosMensalAlocarOCPacienteRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719786L;

	@Override
	public String getTitulo() {
		return "Alocar custo de objetos de custo nos pacientes.";
	}

	@Override
	public String getNome() {
		return "processamentoCustoMensalAlocarOCPaciente - alocarOCPaciente";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 24;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		//Objetos de custos consumidos para a competência com objeto de custo de repasse para o cliente
		List<SigCalculoObjetoCusto> listaCbj = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarCalculosObjetoCustoParaPacientePorCompetencia(sigProcessamentoCusto);
		
		//Permanencias da competência
		List<SigCalculoAtdPermanencia> listaCpp = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaCentroCusto(sigProcessamentoCusto);
		
		//Utilizando por enquanto o de DIETA, mas tem que criar o  tipo APOIO no check do banco 
		SigCategoriaConsumos categoriaConsumo = this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.AP);
		
		//Busca os movimentos de indiretos criados com os cbj (para saber os valores que foram para cada cliente em cada uma das 4 categorias)
		List<SigMvtoContaMensal> movimentos = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarMovimentos(listaCbj, sigProcessamentoCusto);
		
		Map<String, BigDecimal> valoresIndiretos = new HashMap<String, BigDecimal>();
		Set<String> movimentosCentroCustoObjetoCusto = new HashSet<>();
		//Armazena os valores como uma chave de cct + ocv + tipo_Valor
		for (SigMvtoContaMensal movimento : movimentos) {
			String chave = movimento.getFccCentroCustos().getCodigo()+"-"+movimento.getCalculoObjetoCusto().getSigObjetoCustoVersoes().getSeq();
			
			//Armazena o valor do tipo do indireto (II, IP, IE, IS)
			valoresIndiretos.put(chave + "-" + movimento.getTipoValor(), movimento.getValor());
			
			//Informa que existe movimento no centro de custo para o objeto de custo
			movimentosCentroCustoObjetoCusto.add(chave);
		}
		
		Map<Integer, SigCalculoAtdConsumo> consumos = new HashMap<Integer, SigCalculoAtdConsumo>();
		Map<String, BigDecimal> pacienteDiaPorCentroCusto = new HashMap<String, BigDecimal>();
		
		//Para cada permanencia
		for (SigCalculoAtdPermanencia permanencia : listaCpp) {
			
			//Transforma de minutos para dias o tempo de permanência (paciente-dia)
			BigDecimal minutos = permanencia.getTempo();
			BigDecimal horas = ProcessamentoCustoUtils.dividir(minutos, BigDecimal.valueOf(60)); // minutos para horas
			BigDecimal dias = ProcessamentoCustoUtils.dividir(horas, BigDecimal.valueOf(24)); //horas para dias
			BigDecimal qtdeConsumida = dias;
			
			FccCentroCustos centroCusto = permanencia.getCentroCustos(); 
			
			//Para cada objeto de custo
			for (SigCalculoObjetoCusto cbj: listaCbj) {
				
				String chave = centroCusto.getCodigo() + "-" +cbj.getSigObjetoCustoVersoes().getSeq();
				
				//Se possui movimento no centro de custo para o objeto de custo
				if(movimentosCentroCustoObjetoCusto.contains(chave)){
					
					//Verifica se já existe o consumo
					SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarCalculoAtendimentoConsumo(permanencia, cbj.getSigObjetoCustoVersoes(), centroCusto );
					
					//Se não existir, cria um novo com o tempo da permanência e objeto de custo
					if(consumo == null){
						consumo = new SigCalculoAtdConsumo();
						consumo.setCalculoAtividadePermanencia(permanencia);
						consumo.setSigObjetoCustoVersoes(cbj.getSigObjetoCustoVersoes());
						consumo.setQtde(qtdeConsumida);
						consumo.setCriadoEm(new Date());
						consumo.setRapServidores(rapServidores);
						consumo.setCategoriaConsumo(categoriaConsumo);
						consumo.setCentroCustos(centroCusto);
						this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
					}
					//Se existir só atualiza a quantidade
					else{
						consumo.setQtde(consumo.getQtde().add(qtdeConsumida));
						this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
					}
					
					//Armazena o consumo
					consumos.put(consumo.getSeq(), consumo);
					
					//Faz o somatório do paciente-dia no centro de custo para o objeto de custo
					if(pacienteDiaPorCentroCusto.containsKey(chave)){
						pacienteDiaPorCentroCusto.put(chave, pacienteDiaPorCentroCusto.get(chave).add(qtdeConsumida));
					}
					else{
						pacienteDiaPorCentroCusto.put(chave, qtdeConsumida);
					}
				}
			}
			this.commitProcessamentoCusto();	
		}
		
		//Cria registros do vo utilizados na alocação, já que não existe cbj para os cct que existe permanência
		List<ConsumoPacienteConsumidoVO> consumosObjetoCustoApoio = new ArrayList<ConsumoPacienteConsumidoVO>(); 
		for(SigCalculoAtdConsumo consumo : consumos.values()){
			
			ConsumoPacienteConsumidoVO vo = new ConsumoPacienteConsumidoVO();
			
			vo.setCcaSeq(consumo.getSeq());
			vo.setQtdeConsumida(consumo.getQtde());
			
			String chave = consumo.getCentroCustos().getCodigo() + "-" + consumo.getSigObjetoCustoVersoes().getSeq();
			if(pacienteDiaPorCentroCusto.containsKey(chave)){
				vo.setQtdeProduzida(pacienteDiaPorCentroCusto.get(chave));
			}
			
			String chaveEquipamento = chave + "-" + DominioTipoValorConta.IE;
			if(valoresIndiretos.containsKey(chaveEquipamento)){
				vo.setValorIndiretoEquipamento(valoresIndiretos.get(chaveEquipamento));
			}
			
			String chaveInsumo = chave + "-" + DominioTipoValorConta.II;
			if(valoresIndiretos.containsKey(chaveInsumo)){
				vo.setValorIndiretoInsumo(valoresIndiretos.get(chaveInsumo));
			}
			
			String chavePessoal = chave + "-" + DominioTipoValorConta.IP;
			if(valoresIndiretos.containsKey(chavePessoal) ){
				vo.setValorIndiretoPessoal(valoresIndiretos.get(chavePessoal));
			}
			
			String chaveServico = chave + "-" + DominioTipoValorConta.IS;
			if(valoresIndiretos.containsKey(chaveServico)){
				vo.setValorIndiretoServico(valoresIndiretos.get(chaveServico));
			}
			
			consumosObjetoCustoApoio.add(vo);
		}
		
		this.executarAlocarPaciente(consumosObjetoCustoApoio, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	public void executarAlocarPaciente(List<ConsumoPacienteConsumidoVO> consumosObjetoCustoApoio, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos)
			throws ApplicationBusinessException {

		//Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MESSAGEM_ALOCAR_PACIENTES_INICIO");

		//Passo 2
		List<ConsumoPacienteConsumidoVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsumoPacienteConsumidos(sigProcessamentoCusto);

		lista.addAll(consumosObjetoCustoApoio);
		
		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_ALOCAR_PACIENTE_DADOS_OBTIDOS");

		//Passo 5
		for (ConsumoPacienteConsumidoVO vo : lista) {

			//Passo 4
			SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().obterPorChavePrimaria(vo.getCcaSeq());

			//Medio direto
			consumo.setValorMedioDiretoInsumos(this.calcularValorMedio(vo.getValorAtvInsumo(), vo.getValorRateioInsumo(), vo.getQtdeProduzida(), vo.getQtdeConsumida()));

			consumo.setValorMedioDiretoPessoas(this.calcularValorMedio(vo.getValorAtvPessoal(), vo.getValorRateioPessoal(), vo.getQtdeProduzida(),vo.getQtdeConsumida()));

			consumo.setValorMedioDiretoEquipamentos(this.calcularValorMedio(vo.getValorAtvEquipamento(), vo.getValorRateioEquipamento(), vo.getQtdeProduzida(), vo.getQtdeConsumida()));

			consumo.setValorMedioDiretoServicos(this.calcularValorMedio(vo.getValorAtvServico(), vo.getValorRateioServico(), vo.getQtdeProduzida(), vo.getQtdeConsumida()));

			//Medio indireto
			consumo.setValorMedioIndiretoInsumos(this.calcularValorMedio(vo.getValorIndiretoInsumo(), BigDecimal.ZERO, vo.getQtdeProduzida(), vo.getQtdeConsumida()));

			consumo.setValorMedioIndiretoPessoas(this.calcularValorMedio(vo.getValorIndiretoPessoal(), BigDecimal.ZERO, vo.getQtdeProduzida(), vo.getQtdeConsumida()));

			consumo.setValorMedioIndiretoEquipamentos(this.calcularValorMedio(vo.getValorIndiretoEquipamento(), BigDecimal.ZERO, vo.getQtdeProduzida(), vo.getQtdeConsumida()));

			consumo.setValorMedioIndiretoServicos(this.calcularValorMedio(vo.getValorIndiretoServico(), BigDecimal.ZERO, vo.getQtdeProduzida(), vo.getQtdeConsumida()));
			
			//Total
			consumo.setValorTotalInsumos(consumo.getValorMedioDiretoInsumos().add( consumo.getValorMedioIndiretoInsumos()).add(consumo.getValorParcialConsumidoInsumos()));
			
			consumo.setValorTotalPessoas(consumo.getValorMedioDiretoPessoas().add( consumo.getValorMedioIndiretoPessoas()).add(consumo.getValorParcialConsumidoPessoas()));
			
			consumo.setValorTotalEquipamentos(consumo.getValorMedioDiretoEquipamentos().add( consumo.getValorMedioIndiretoEquipamentos()).add(consumo.getValorParcialConsumidoEquipemantos()));
			
			consumo.setValorTotalServicos(consumo.getValorMedioDiretoServicos().add( consumo.getValorMedioIndiretoServicos()).add(consumo.getValorParcialConsumidoServicos()));

			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		this.commitProcessamentoCusto();

		//Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_ALOCAR_PACIENTE_SUCESSO");
	}

	private BigDecimal calcularValorMedio(BigDecimal valorAtv, BigDecimal valorRateio, BigDecimal qtdeProduziada, BigDecimal qtdeConsumida) {
		if(qtdeProduziada != null && !qtdeProduziada.equals(BigDecimal.ZERO)){
			return ProcessamentoCustoUtils.dividir(valorAtv.add(valorRateio), qtdeProduziada).multiply(qtdeConsumida) ;
		}
		else{
			return BigDecimal.ZERO;
		}
	}
}
