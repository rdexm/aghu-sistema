package br.gov.mec.aghu.internacao.business;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.responsaveispaciente.business.IResponsaveisPacienteFacade;
import br.gov.mec.aghu.internacao.vo.ResponsaveisPacienteVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioResponsaveisPacienteON extends BaseBusiness {


@EJB
private InternacaoON internacaoON;

@EJB
private InternacaoRN internacaoRN;

private static final Log LOG = LogFactory.getLog(RelatorioResponsaveisPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@EJB
private IResponsaveisPacienteFacade responsaveisPacienteFacade;

@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

@EJB
private IPacienteFacade pacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5289886596047396296L;

	private enum RelatorioResponsaveisPacienteONExceptionCode implements
	BusinessExceptionCode {
		AIN_PACIENTE_SEM_OCUPACAO, AIN_ERRO_GERACAO_CONTRATO, AIN_INTERNACAO_SEM_CONVENIO, AIN_INTERNACAO_SEM_CONTA_INTERNACAO,
		AIN_CONVENIO_CONTA_DIFERENTE_CONVENIO_INTERNACAO, AIN_CONTA_INTERNACAO_ENCERRADA, AIN_INFORMAR_RESPONSAVEL_CONTRATO
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private List<ResponsaveisPacienteVO> atribuirValoresRelatorioResponsaveisPacienteVO(
			List<String> listaDescricoes, AinInternacao internacao)  throws ApplicationBusinessException {
		List<ResponsaveisPacienteVO> listaResponsaveisPacienteVO = new ArrayList<ResponsaveisPacienteVO>();

		StringBuffer descricaoTotal = new StringBuffer(70);

		for (String descricao: listaDescricoes){
			descricaoTotal.append("       <style isBold='true' pdfFontName='Courier-Bold'>").append(descricao).append("</style>");
			descricaoTotal.append('\n').append(obterDadosFixoContrato()).append('\n');
			// descricaoTotal.append('\n').append(obterDadosFixoContrato()).append('\n');
		}

		// Cria um objeto calendar com a data atual
		Calendar today = Calendar.getInstance();
		// Obtém a idade baseado no ano
		int dia = today.get(Calendar.DAY_OF_MONTH);
		int mes = today.get(Calendar.MONTH);
		//Corrige o mês (pois não existe o mês zero)
		mes++;
		int ano = today.get(Calendar.YEAR);
		String strMes = CoreUtil.obterMesPorExtenso(mes);

		StringBuilder dadosPaciente = new StringBuilder(900);
		StringBuilder nomePaciente = new StringBuilder();
		StringBuilder nomeResponsavel = new StringBuilder();
		
		String cpfResponsavel = "           ";
		String ciResponsavel = "          ";
		
		String nomeRes = "";
		AinResponsaveisPaciente responsavelContratante = this.getResponsaveisPacienteFacade().obterResponsaveisPacienteTipoConta(internacao.getSeq());
		if (responsavelContratante == null){
			throw new ApplicationBusinessException(RelatorioResponsaveisPacienteONExceptionCode.AIN_INFORMAR_RESPONSAVEL_CONTRATO);
		}
		else {
  			if(responsavelContratante.getResponsavelConta()!=null) {
  				AghResponsavel aghResp = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(responsavelContratante.getResponsavelConta().getSeq());
  				if(aghResp.getAipPaciente()!=null) {
  					nomeRes = aghResp.getAipPaciente().getNome();
  					if (aghResp.getAipPaciente().getCpf() != null){
  						cpfResponsavel = aghResp.getAipPaciente().getCpf().toString();
  					}
  					if (aghResp.getAipPaciente().getRg() != null){
  						ciResponsavel = aghResp.getAipPaciente().getRg().toString();
  					}
  				}
  				else {
  					nomeRes = aghResp.getNome();
  					if (aghResp.getCpfCgc() != null){
  						cpfResponsavel = aghResp.getCpfCgc().toString();
  					}
  					if (aghResp.getRg() != null){
  						ciResponsavel = aghResp.getRg().toString();
  					}
  				}
  			}
  			else {
  				nomeRes = responsavelContratante.getNome();
  				if(responsavelContratante.getCpf()!=null) {
  					cpfResponsavel = responsavelContratante.getCpf().toString();
  				}
  				if(responsavelContratante.getRg()!=null) {
  					ciResponsavel = responsavelContratante.getRg().toString();
  				}
  			}
		}

		// Cálculo do Valor UTI por dia
		Date dthrInternacao = internacao.getDthrInternacao();
		Double valorUTI = this.getInternacaoRN().verificarValorAcomodacao(0, dthrInternacao);

		Double valorLeito = null;
		// Cálculo do Valor de Leito 
		if (internacao.getLeito() != null){
			valorLeito = this.calcularValorLeito(internacao);
		}
		

		String leito = "";
		if (internacao.getLeito() != null) {
			leito = StringUtils.rightPad(internacao.getLeito().getLeitoID(), 15, ' ');
		}

		dadosPaciente.append("\n  23. Convênio: ").append(internacao.getConvenioSaudePlano().getConvenioSaude().getDescricao());
		if (internacao.getFatContasInternacao() != null && internacao.getFatContasInternacao().size() > 0) {
			FatContasHospitalares fatContasHospitalares = internacao.getFatContasInternacao().iterator().next().getContaHospitalar();
			if (fatContasHospitalares != null && fatContasHospitalares.getAcomodacao() != null) {
			String descricao = internacao.getFatContasInternacao().iterator().next().getContaHospitalar().getAcomodacao().getDescricao();
			dadosPaciente.append(descricao == null ? "" : " " + descricao);
		}
		}
		dadosPaciente.append("\n  Valores de Diárias(cobrança à parte de materiais, medicamentos, exames, taxas):");
		if (valorLeito != null){
			dadosPaciente.append("\n          Leito:  ").append(leito).append(" ...........R$ ").append(this.formatarNumeroMoeda(valorLeito));	
		}
			
		dadosPaciente.append("\n          CTICC / UTI POR DIA: ..............R$ ").append(this.formatarNumeroMoeda(valorUTI));

		if(internacao.getIndDifClasse()) {
			dadosPaciente.append("\n\n  Observações:\n          PACIENTE OPTOU POR DIFERENÇA DE CLASSE");
		}
		
		dadosPaciente.append(" \n\n   E, por estarem de comum acordo, firmam este instrumento em 2 (duas) vias de igual teor e forma.")

		.append(" \n\n                               Porto Alegre, ").append(dia).append(" de ").append(strMes).append(" de ").append(ano)
		.append(" \n                               Hospital de Clínicas de Porto Alegre \n\n_________________________________       _________________________________");

		if (internacao.getPaciente().getNome().length() > 25){
			nomePaciente.append(internacao.getPaciente().getNome().substring(0, 25));
		} else {
			nomePaciente.append(internacao.getPaciente().getNome());	
		}

		if (nomeRes.length() > 23){
			nomeResponsavel.append(nomeRes.substring(0, 23));
		} else {
			nomeResponsavel.append(nomeRes);	
		}

		int lengthNomePaciente = nomePaciente.length();

		for (int i=0; i<(25 - lengthNomePaciente);i++){
			nomePaciente.append(' ');
		}	

		//Espaço entre o nome do paciente e o do responsável
		nomePaciente.append("      ");

		dadosPaciente.append("\nPaciente ").append(nomePaciente).append("Responsável ").append(nomeResponsavel);

		String cpfPaciente = "           ";
		if (internacao.getPaciente().getCpf() != null){
			cpfPaciente = internacao.getPaciente().getCpf().toString();
		}
		String ciPaciente = "          ";
		if (internacao.getPaciente().getRg() != null && !internacao.getPaciente().getRg().isEmpty()){
			ciPaciente = internacao.getPaciente().getRg();
		}

		dadosPaciente.append("\nCPF ").append(cpfPaciente).append("    CI ").append(ciPaciente).append("        CPF ").append(cpfResponsavel).append("    CI ").append(ciResponsavel)
		.append("\n\nTestemunhas:\n\n_________________________________       _________________________________")
		.append("\nNome:                                   Nome:\nCPF:                                    CPF:\nCI:                                     CI:");
		
		descricaoTotal.append(dadosPaciente);

		ResponsaveisPacienteVO relatorio = new ResponsaveisPacienteVO();
		relatorio.setDescContrato(descricaoTotal.toString());
		relatorio.setDadosPaciente(dadosPaciente.toString());
		listaResponsaveisPacienteVO.add(relatorio);
		return listaResponsaveisPacienteVO;
	}

	private String formatarNumeroMoeda(Double valor) {
		if (valor == null) {
			return "";
		} else {
			Locale loc = new Locale("pt", "BR");
			NumberFormat nb = NumberFormat.getInstance(loc);
			nb.setMinimumFractionDigits(2);
			nb.setMaximumFractionDigits(2);

			return nb.format(valor);
		}
	}

	private String obterDadosFixoContrato() {
		String abreNegrito = "<style isBold='true' pdfFontName='Courier-Bold'>";
		String fechaNegrito = "</style>";
		String dadosFixo = "         Com relação ao contratado, o paciente usuário dos serviços de saúde prestados pelo HCPA e seu(sua) responsável, estão cientes de que ao optar o primeiro(a) por receber atendimento nesta instituição, estarão submetidos às seguintes cláusulas e condições:\n"
			+ "1. O paciente e o responsável, no ato da internação, ou a qualquer momento, terão acesso às informações sobre a tabela de preços vigente no HCPA, que estará disponível na Seção de Convênios.\n"
			+ "2. Os preços a serem cobrados serão aqueles vigentes na tabela do HCPA no dia da internação, permanecendo inalterados por um período de 15 (quinze) dias. Os preços constantes da tabela sofrerão reajuste de acordo com a legislação vigente.\n"
			+ "3. O paciente e o responsável " + abreNegrito + "que optarem por internação particular ou na modalidade diferença de classe " + fechaNegrito + "efetuarão, quando solicitado, depósito inicial estipulado na tabela de preços do HCPA para o tipo de internação que optarem neste ato, tendo em vista a cobertura das despesas médico-hospitalares.\n"
			+ "4. O paciente beneficiário de Convênio e seu responsável estão cientes que ao optarem por acomodações diferenciadas " + abreNegrito + "assumirão a responsabilidade pelo pagamento de todas as despesas hospitalares não cobertas pelo Convênio." + fechaNegrito + "\n"
			+ "5. O HCPA solicitará depósito complementar à medida que as despesas com o paciente venham a ser superiores ao valor do depósito inicial.\n"
			+ "6. O paciente beneficiário de Convênio deverá:\n"
			+ "6.1 Apresentar Guia da Internação autorizada ou senha liberada pela Entidade Conveniada no ato da internação.\n"
			+ "6.2  Providenciar junto à Entidade Conveniada a prorrogação da Guia de Internação, sempre que o período de hospitalização for superior ao número de dias autorizados pelo Convênio.\n"
			+ "PARÁGRAFO ÚNICO: O paciente que não possuir a documentação referida no item 6.1 internará como paciente PARTICULAR e terá o prazo de até 48 horas de dia útil para providenciá-la junto à Entidade Conveniada. Esgotado este prazo, assumirá definitivamente a condição de Paciente Particular e deverá efetuar depósito prévio na forma dos itens 3 e 4 do presente instrumento.\n"
			+ "7. Os cheques deixados como depósito irão para compensação no ato da internação do paciente.\n"
			+ "8. Na eventualidade de ocorrer saldo credor na conta do paciente, a restituição do depósito dado em garantia será feita no momento da alta, já deduzidos os gastos do paciente durante a internação.\n"
			+ "9. O acerto de contas que implicar devolução de numerário somente será efetuado em horário bancário, e não se efetuará enquanto pender compensação de cheques.\n"
			+ "10. O paciente internado através de Convênio e seu responsável, cientes da cobertura de seu Plano de Saúde, responsabilizam-se em ressarcir diretamente ao HCPA quaisquer despesas não pagas pela Entidade Conveniada.\n"
			+ "11. Quando da transferência do paciente para CTICC/UTI, caso o acompanhante queira manter a acomodação, será cobrada a diária correspondente, com acréscimo de 100%.\n"
			+ "12. Independentemente da hora em que ocorrer a internação hospitalar será cobrada diária inteira, sendo válida até às 12 horas do dia seguinte.\n"
			+ "13. Os honorários devidos aos médicos assistentes serão por eles estabelecidos e pagos diretamente pelos pacientes sem interferência ou responsabiliadade do HCPA.\n"
			+ "14. Tanto o paciente quanto o seu responsável ficam obrigados a efetuar o pagamento do total das despesas de sua internação ou procedimentos descritos na conta hospitalar, no momento da alta hospitalar.\n"
			+ "15. Quando o responsável pela internação não for o próprio paciente, ficarão ambos(responsável e paciente) solidariamente responsáveis pelo pagamento das despesas decorrentes da internação hospitalar.\n"
			+ "16. Caso o HCPA seja obrigado a recorrer à via judicial para cobrar as despesas de hospitalização, poderá propor a respectiva ação tanto contra o paciente quanto contra o responsável ou contra os dois, pela internação, não podendo nenhum deles invocar benefícios de qualquer ordem.\n"
			+ "17. O HCPA debitará ao paciente e/ou responsável os danos causados em móveis, utensílios e bens de sua propriedade.\n"
			+ "18. Eventuais valores não liquidados na alta do paciente serão acrescidos de multa de 2%, juros legais e atualização monetária.\n"
			+ "19. O HCPA não se responsabiliza pelo desaparecimento de dinheiro, jóias ou quaisquer objetos de valor trazidos para suas dependências pelo paciente, responsáveis ou visitantes, bem assim por veículos estacionados em sua propriedade, por eleição voluntária de seus donos e o que neles estiverem.\n"
			+ "20. Durante a internação no HCPA o paciente e seu responsável submeter-se-ão as determinações administrativas e médicas recomendadas para o tratamento ou esclarecimento do diagnóstico, estabelecido desde já, que todas as despesas havidas e registradas no prontuario médico, são presumidas líquidas, certas e exigíveis.\n"
			+ "21. Fica eleito o foro da Justiça Federal de Porto Alegre para dirimir qualquer litígio decorrente do presente instrumento.\n"
			+ "22. Isto posto, o paciente e o responsável, abaixo-assinados, declaram pelo presente e na melhor forma de direito, ter pleno conhecimento das cláusulas registradas neste documento e responsabilizam-se inteiramente pelas despesas decorrentes da internação e/ou procedimentos cometidos, sendo certo que em cobrança judicial serão cobrados, ainda, honorários advocatícios de 20% (vinte por cento) sobre o valor total do débito mais o reembolso de custas e despesas havidas com a cobrança, inclusive cartorárias e de protesto.";

		return dadosFixo;
	}

	/**
	 * Método responsável pela emissão do contrato de
	 * hospitalização.
	 * 
	 * @param responsavelPaciente
	 *  
	 */
	public List<ResponsaveisPacienteVO> obterRelatorioResponsaveisPaciente(Integer intSeq) throws ApplicationBusinessException{

		String descricaoQ1 = " Entre o HOSPITAL DE CLÍNICAS DE PORTO ALEGRE, empresa pública federal criada pela Lei 5.604/70,"
			+ " com sede em Porto Alegre na Rua Ramiro Barcelos nº 2350," 
			+ " Largo Eduardo Zaccaro Faraco, CEP 90035-003, regularmente inscrita  no  CNPJ/MF nº 87.020.517/0001-20,"
			+ " neste ato denominado HCPA e, #NOME_PACIENTE#, #DESCRICAO_OCUPACAO#, #DESCRICAO_ESTADO_CIVIL#,"
			+ " portador do CPF #CPF_PACIENTE#, filho de #NOME_MAE_PACIENTE#, residente à #DESCRICAO_ENDERECO_PACIENTE#,"
			+ " #TELEFONE_PACIENTE# adiante denominado simplesmente PACIENTE e, ainda,"
			+ " #NOME_RESPONSAVEL#, portador do CPF #CPF_RESPONSAVEL#, filho de #NOME_MAE_RESPONSAVEL#, residente à"
			+ " #DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#, adiante denominado simplesmente RESPONSÁVEL, resolvam de"
			+ " comum acordo, firmar o presente contrato de Prestação de Serviços Médicos.";

		AinInternacao internacao = obterDadosContrato(intSeq);

		List<String> listaDescricoes = parametrizarTextoFixo(descricaoQ1, internacao);
		List<ResponsaveisPacienteVO> listaResponsaveisPacienteVO = atribuirValoresRelatorioResponsaveisPacienteVO(listaDescricoes, internacao);
		return listaResponsaveisPacienteVO;
	}

	@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.NPathComplexity"})
	private List<String> parametrizarTextoFixo(final String descricaoQ1, AinInternacao internacao) throws ApplicationBusinessException{
		List<String> listaDescricoes = new ArrayList<String>();
		if (internacao != null){

			List<AinResponsaveisPaciente> listaResponsaveisSalvosNoBanco = this.getResponsaveisPacienteFacade().pesquisarResponsaveisPaciente(internacao.getSeq());
			for (AinResponsaveisPaciente resp: listaResponsaveisSalvosNoBanco){
				if (resp.getTipoResponsabilidade() == DominioTipoResponsabilidade.C || resp.getTipoResponsabilidade() == DominioTipoResponsabilidade.CT) {
				String descricaoParametrizada = "";
				descricaoParametrizada = descricaoQ1.replace("#NOME_PACIENTE#", internacao.getPaciente().getNome());

				if (internacao.getPaciente().getAipOcupacoes() != null) {
					descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_OCUPACAO#", internacao.getPaciente().getAipOcupacoes().getDescricao().toLowerCase());
				} else {
					descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_OCUPACAO#", "");
				}

				if (internacao.getPaciente().getEstadoCivil() != null){
					descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ESTADO_CIVIL#", internacao.getPaciente().getEstadoCivil().getDescricao().toLowerCase());  				
				}
				else{
					descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ESTADO_CIVIL#", "");
				}
				if (internacao.getPaciente().getCpf() != null){
					descricaoParametrizada = descricaoParametrizada.replace("#CPF_PACIENTE#", internacao.getPaciente().getCpf().toString());  				
				}
				else{
					descricaoParametrizada = descricaoParametrizada.replace("#CPF_PACIENTE#", "");  	
				}
				descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_PACIENTE#", internacao.getPaciente().getNomeMae());
				String descricaoEnderecoPaciente = obterDescricaoEnderecoResidencial(internacao.getPaciente());
				descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_PACIENTE#", descricaoEnderecoPaciente);  				
				if (internacao.getPaciente().getFoneResidencial() != null){
					descricaoParametrizada = descricaoParametrizada.replace("#TELEFONE_PACIENTE#", "telefone " + (internacao.getPaciente().getDddFoneResidencial() == null ? "" : internacao.getPaciente().getDddFoneResidencial())  + " " + internacao.getPaciente().getFoneResidencial() + ", ");  				
				}
				else{
					descricaoParametrizada = descricaoParametrizada.replace("#TELEFONE_PACIENTE#", "");
				}
				/*	#48927 - Adequar para obter os dados do responsável (nome, nome_mae, cpf, rg, endereco, telefone) em agh_responsaveis,
				quando ain_responsaveis_pacientes.res_seq não for nula.
				Neste caso, avaliar se agh_responsaveis.pac_codigo não nulo, os dados devem ser obtidos de aip_pacientes
				com pac.codigo = agh_responsaveis.pac_codigo.*/
				if(resp.getResponsavelConta()!=null) {
					AghResponsavel responsavel = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(resp.getResponsavelConta().getSeq());
					if(responsavel.getAipPaciente()!=null) {
						AipPacientes pacienteResp = pacienteFacade.obterAipPacientesPorChavePrimaria(responsavel.getAipPaciente().getCodigo());
						descricaoParametrizada = descricaoParametrizada.replace("#NOME_RESPONSAVEL#", pacienteResp.getNome());
						if(pacienteResp.getCpf()!=null) {
							descricaoParametrizada = descricaoParametrizada.replace("#CPF_RESPONSAVEL#", pacienteResp.getCpf().toString());
						}
						else {
							descricaoParametrizada = descricaoParametrizada.replace("#CPF_RESPONSAVEL#", ""); 
						}
						if(pacienteResp.getNomeMae()!=null) {
							descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_RESPONSAVEL#", pacienteResp.getNomeMae());
						}
						else {
							descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_RESPONSAVEL#", ""); 
						}
						String descricaoEnderecoResponsavel = obterDescricaoEnderecoResidencial(pacienteResp);
						if (descricaoEnderecoResponsavel != null){
							descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#", descricaoEnderecoResponsavel);  				
						} else{
							descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#", descricaoEnderecoPaciente);
						}  	
					}
					else {
						descricaoParametrizada = descricaoParametrizada.replace("#NOME_RESPONSAVEL#", responsavel.getNome());
						if(responsavel.getCpfCgc()!=null) {
							descricaoParametrizada = descricaoParametrizada.replace("#CPF_RESPONSAVEL#", responsavel.getCpfCgc().toString());
						}
						else {
							descricaoParametrizada = descricaoParametrizada.replace("#CPF_RESPONSAVEL#", ""); 
						}
						if(responsavel.getNomeMae()!=null) {
							descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_RESPONSAVEL#", responsavel.getNomeMae());
						}
						else {
							descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_RESPONSAVEL#", ""); 
						}
						String descricaoEnderecoResponsavel = obterDescricaoEnderecoResidencialAghResponsavel(responsavel);
						if (descricaoEnderecoResponsavel != null){
							descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#", descricaoEnderecoResponsavel);  				
						} else{
							descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#", descricaoEnderecoPaciente);
						}
					}
				}
				else {
					descricaoParametrizada = descricaoParametrizada.replace("#NOME_RESPONSAVEL#", resp.getNome());
					if (resp.getCpf() != null){
						descricaoParametrizada = descricaoParametrizada.replace("#CPF_RESPONSAVEL#", resp.getCpf().toString());  				
					}
					else{
						descricaoParametrizada = descricaoParametrizada.replace("#CPF_RESPONSAVEL#", "");  
					}
					if (resp.getNomeMae() != null){
						descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_RESPONSAVEL#", resp.getNomeMae());  				
					}
					else{
						descricaoParametrizada = descricaoParametrizada.replace("#NOME_MAE_RESPONSAVEL#", "");  
					}
					String descricaoEnderecoResponsavel = obterDescricaoEnderecoResidencialResponsavel(resp);
					if (descricaoEnderecoResponsavel != null){
						descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#", descricaoEnderecoResponsavel);  				
					} else{
						descricaoParametrizada = descricaoParametrizada.replace("#DESCRICAO_ENDERECO_RESPONSAVEL_OU_PACIENTE#", descricaoEnderecoPaciente);
					}
				}
				//descricaoParametrizada+= "\n\n\n" + obterDadosFixoContrato();
				listaDescricoes.add(descricaoParametrizada);
			}
		}
		}
		return listaDescricoes;
	}

	private AinInternacao obterDadosContrato(Integer intSeq) throws ApplicationBusinessException{
		AinInternacao internacao = this.getAinInternacaoDAO().obterInternacaoJoinPaciente(intSeq);

		if (internacao == null){
			throw new ApplicationBusinessException(RelatorioResponsaveisPacienteONExceptionCode.AIN_ERRO_GERACAO_CONTRATO);
		}

		if (internacao.getPaciente().getAipOcupacoes() == null){
			throw new ApplicationBusinessException(RelatorioResponsaveisPacienteONExceptionCode.AIN_PACIENTE_SEM_OCUPACAO);
		}
		if (internacao.getConvenioSaudePlano() == null){
			throw new ApplicationBusinessException(RelatorioResponsaveisPacienteONExceptionCode.AIN_INTERNACAO_SEM_CONVENIO);
		}

		List<FatContasInternacao> listaContasInternacao = this.getFaturamentoFacade().pesquisarContaInternacaoPorInternacao(internacao.getSeq());

		if (listaContasInternacao.size() == 0){
			throw new ApplicationBusinessException(RelatorioResponsaveisPacienteONExceptionCode.AIN_INTERNACAO_SEM_CONTA_INTERNACAO);
		}

		return internacao;
	}

	/**
	 * Retorna uma descrição do endereço residencial do paciente para
	 * emissão do contrato de hospitalização
	 * @param pacCodigo código do paciente.
	 * @return
	 */
	private String obterDescricaoEnderecoResidencial(AipPacientes paciente) {
		//String descricaoEndereco = null;
		StringBuffer descricaoEndereco = new StringBuffer();
		for(AipEnderecosPacientes endereco: paciente.getEnderecos()){
			if (endereco.getTipoEndereco() == DominioTipoEndereco.R){
			    if (StringUtils.isNotBlank(endereco.getLogradouro())){
			    	descricaoEndereco.append(endereco.getLogradouro()).append(' ').append(endereco.getNroLogradouro());
			    	if (endereco.getAipCidade() != null){
						descricaoEndereco.append(", ").append(
								endereco.getAipCidade().getNome()).append(", ")
								.append(endereco.getAipCidade().getAipUf().getSigla());
		    		
			    	}
			    	else{
			    		if (StringUtils.isNotBlank(endereco.getCidade())){
			    			descricaoEndereco.append(", ").append(
									endereco.getCidade());
			    		}
			    	}
			    }
			    else{
			    	descricaoEndereco.append(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getNome()).append(' ')
			    	.append(endereco.getNroLogradouro()).append(", ").append(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getNome())
			    	.append(", ").append(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getAipUf().getSigla());		    	
			    }
			    break;

			}
		}
		return descricaoEndereco.toString();
	}


	private String obterDescricaoEnderecoResidencialResponsavel(AinResponsaveisPaciente responsavelPaciente) {
		String descricaoEndereco = null;
		if (responsavelPaciente.getLogradouro() != null){
			descricaoEndereco = responsavelPaciente.getLogradouro() + (responsavelPaciente.getCidade() == null ? "" : (", " + responsavelPaciente.getCidade())) + ", telefone " + 
			(responsavelPaciente.getDddFone() == null ? "" : responsavelPaciente.getDddFone()) + " " + (responsavelPaciente.getFone() == null ? "" : responsavelPaciente.getFone());
		}
		return descricaoEndereco;
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	private String obterDescricaoEnderecoResidencialAghResponsavel(AghResponsavel responsavelPaciente) {
		String descricaoEndereco = null;
		if (responsavelPaciente.getAipBairrosCepLogradouro() != null) {
			descricaoEndereco = responsavelPaciente.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getNome() +  (responsavelPaciente.getNroLogradouro() == null ? "" : (" " + responsavelPaciente.getNroLogradouro())) + (responsavelPaciente.getAipCidade() == null ? "" : (", " + responsavelPaciente.getAipCidade().getNome())) + ", " +
									responsavelPaciente.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getAipUf().getSigla() + ", telefone " +
										(responsavelPaciente.getDddFone() == null ? "" : responsavelPaciente.getDddFone()) + " " + (responsavelPaciente.getFone() == null ? "" : responsavelPaciente.getFone());
		}
		else {
			if (responsavelPaciente.getLogradouro() != null){
				descricaoEndereco = responsavelPaciente.getLogradouro() + (responsavelPaciente.getNroLogradouro() == null ? "" : (" " + responsavelPaciente.getNroLogradouro())) + (responsavelPaciente.getAipCidade() == null ? "" : (", " + responsavelPaciente.getAipCidade().getNome())) + (responsavelPaciente.getAipCidade().getAipUf() == null ? "" : (", " + responsavelPaciente.getAipCidade().getAipUf().getSigla())) + ", telefone " +
						(responsavelPaciente.getDddFone() == null ? "" : responsavelPaciente.getDddFone()) + " " + (responsavelPaciente.getFone() == null ? "" : responsavelPaciente.getFone());
			}
		}
		return descricaoEndereco;
	}


	/**
	 * Método para calcular o valor do leito a ser apresentado no relatório com
	 * no SELECT da query original feita no relatório do AGH.
	 */
	private Double calcularValorLeito(AinInternacao internacao)
	throws ApplicationBusinessException {

		Double retorno = Double.valueOf(0);
		if (internacao != null) {
			if (Boolean.TRUE.equals(internacao.getIndDifClasse())) {
				// Calcula o valor da acomodação se o IND_DIF_CLASSE == 'S'
				AinAcomodacoes acomodacao = null;
				if (internacao.getLeito() != null && internacao.getLeito().getQuarto() != null) {
					acomodacao = this.getInternacaoON().obterAcomodacao(internacao
							.getLeito().getQuarto().getAcomodacao().getSeq());
				}

				Double valorAcomodacao = this.getInternacaoRN().verificarValorAcomodacao(
						acomodacao.getSeq(), DateUtils.truncate(internacao
								.getDthrInternacao(), Calendar.DAY_OF_MONTH));

				Double valorDiminuido = null;

				try {
					// Busca acomodação através da conta de internação e da
					// conta hospitala
					acomodacao = this.getInternacaoON().obterAcomodacaoContaHospitalar(
							internacao.getSeq(), internacao
							.getConvenioSaudePlano().getId()
							.getCnvCodigo());

					// Se a acomodação for null, não valida demais dados
					if (acomodacao != null) {
						Integer codigoAcomodacao = acomodacao == null ? null
								: acomodacao.getSeq();
						Byte cspSeq = internacao.getConvenioSaudePlano().getId()
						.getSeq();
						Short cspCnvCodigo = internacao.getConvenioSaudePlano()
						.getId().getCnvCodigo();

						// Busca parametro com código do convenio
						AghParametros parametro = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_COD_CNV_CONTRATO_PACIENTE);
						Integer convenioParametro = parametro.getVlrNumerico()
						.intValue();

						// Busca parametro com data base para ser usada caso o
						// convenio seja igual ao do parametro
						parametro = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_DATA_CNV_CONTRATO_PACIENTE);
						Date dataParametro = parametro.getVlrData();

						Date data = null;
						if (convenioParametro.equals(cspCnvCodigo)) {
							data = dataParametro;
						} else {
							data = internacao.getDthrInternacao();
						}

						Integer numeroAcompanhantes = this.getInternacaoRN()
						.verificarNumeroAcompanhantes(internacao.getSeq());

						valorDiminuido = this.getInternacaoRN()
						.calcularValorAcomodacaoConvenio(codigoAcomodacao,
								cspCnvCodigo, cspSeq, DateUtils.truncate(
										data, Calendar.DAY_OF_MONTH),
										numeroAcompanhantes);
					}
				} catch (Exception e) {
					this.logWarn(e.getMessage());
					valorDiminuido = Double.valueOf(0);
				}
				
				if (valorAcomodacao != null && valorDiminuido != null){
					retorno = valorAcomodacao - valorDiminuido;
				}
				else {
					retorno = Double.valueOf(0);
				}
				

			} else {
				// Calcula o valor da acomodação se o IND_DIF_CLASSE <> 'S'
				AinAcomodacoes acomodacao = null;

				if (internacao.getFatContasInternacao() != null && internacao.getFatContasInternacao().size() > 0) {
					acomodacao = internacao.getFatContasInternacao().iterator().next().getContaHospitalar().getAcomodacao();					
				}
				if (acomodacao == null) {
					if (internacao.getLeito() != null) {
						acomodacao = internacao.getLeito().getQuarto()
						.getAcomodacao();
					} else if (internacao.getQuarto() != null) {
						acomodacao = internacao.getQuarto().getAcomodacao();
					}
				}
				if (acomodacao != null) {
					Double valorAcomodacao = this.getInternacaoRN()
					.verificarValorAcomodacao(acomodacao.getSeq(),
							internacao.getDthrInternacao());
					retorno = valorAcomodacao;
				}
			}
		}

		return retorno;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IResponsaveisPacienteFacade getResponsaveisPacienteFacade() {
		return responsaveisPacienteFacade;
	}

	protected InternacaoON getInternacaoON() {
		return internacaoON;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

}
