package br.gov.mec.aghu.internacao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteDAO;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAreaAtuacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;

@Stateless
public class RelatorioInternacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioInternacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5783703656665610708L;

	/**
	 * Método para verificar se uma unidade funcional tem uma determinada
	 * característica.
	 * 
	 * ORADB AGHC_VER_CARACT_UNF
	 * 
	 * @param seq
	 * @param caracteristica
	 * @return true/false
	 */
	public Boolean verificarCaracteristicaUnidadeFuncional(Short seq,
			ConstanteAghCaractUnidFuncionais caracteristica) {

		if (seq == null || caracteristica == null) {
			return false;
		} else {
			List<AghCaractUnidFuncionais> caracteristicaList = this
					.pesquisarCaracteristicaUnidadeFuncional(seq,
							caracteristica);

			return caracteristicaList.size() > 0;
		}
	}

	/**
	 * Método para fazer busca de unidade funcional pelo seu seq e sua
	 * característica.
	 * 
	 * @param seq
	 *            da unidade funcional
	 * @param caracteristica
	 * @return lista de unidades funcionais
	 */
	private List<AghCaractUnidFuncionais> pesquisarCaracteristicaUnidadeFuncional(
			Short seq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return getAghuFacade().listaCaracteristicasUnidadesFuncionaisPaciente(seq, caracteristica);
	}

	/**
	 * Método para obter a internação pelo seu ID (seq).
	 * 
	 * @param seq
	 * @return
	 */
	public AinInternacao obterInternacao(Integer seq) {
		return getAinInternacaoDAO().obterPorChavePrimaria(seq);
	}

	/**
	 * Método para formatar o telefone do paciente com o DDD e FONE. Utiliza o
	 * telefone de recados caso não exista um telefone residencial
	 * 
	 * @param dddFoneResidencial
	 * @param foneResidencial
	 * @param dddFoneRecado
	 * @param foneRecado
	 * @return String com telefone formatado (Ex.: "051 33558888")
	 */
	public String formatarTelefone(String dddFoneResidencial,
			String foneResidencial, String dddFoneRecado, String foneRecado) {

		StringBuffer telefone = new StringBuffer();
		if (foneResidencial != null && !"".equals(foneResidencial)) {
			telefone.append(ObjectUtils.toString(dddFoneResidencial));
			if (telefone.length() > 0) {
				telefone.append(' ');
			}
			telefone.append(ObjectUtils.toString(foneResidencial));
		} else if (foneRecado != null && !"".equals(foneRecado)) {
			telefone.append(ObjectUtils.toString(dddFoneRecado));
			if (telefone.length() > 0) {
				telefone.append(' ');
			}
			telefone.append(ObjectUtils.toString(foneRecado));
		}

		return telefone.toString();
	}
	
	/**
	 * AIPC_GET_FONE_PAC
	 * @param pacCodigo
	 * @return
	 */
	public String obterTelefonePaciente(Integer pacCodigo) {
		AipPacientes pac = getPacienteFacade().obterPaciente(pacCodigo);
		if (pac != null) {
			return formatarTelefone(
					ObjectUtils.toString(pac.getDddFoneResidencial()),
					ObjectUtils.toString(pac.getFoneResidencial()),
					ObjectUtils.toString(pac.getDddFoneRecado()),
					ObjectUtils.toString(pac.getFoneRecado()));
		}
		return ""; 
	}

	/**
	 * Método para pesquisar o posto de saúde de um paciente através dos dados
	 * de seus endereços.
	 * 
	 * ORADB MAMC_GET_POSTO_PAC
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public String obterPostoSaudePaciente(Integer codigoPaciente) {
		String nomePosto = null;
		Integer codigoCidadePadrao;

		try {
			AghParametros parametro = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_CODIGO_CIDADE_PADRAO);
			codigoCidadePadrao = parametro.getVlrNumerico().intValue();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			codigoCidadePadrao = null;
		}

		if (codigoCidadePadrao != null) {
			AipPacientes paciente = getPacienteFacade().obterPaciente(codigoPaciente);
			if (paciente != null && paciente.getEnderecos() != null) {
				// Percorre lista de endereços do paciente, buscando pelo
				// endereço com tipo = 'R'
				for (AipEnderecosPacientes endPaciente : paciente
						.getEnderecos()) {
					if (DominioTipoEndereco.R.equals(endPaciente
							.getTipoEndereco())) {

						nomePosto = this.verificarDadosEndereco(endPaciente,
								codigoCidadePadrao);

						if (nomePosto != null && !"".equals(nomePosto)) {
							break;
						}
					}
				}
			}
		}
		return nomePosto;
	}

	/**
	 * Método para verificar dados do endereço e retornar o nome do posto com
	 * base no endereço do paciente e do código da cidade padrão, vindo dos
	 * parametros do sistema.
	 * 
	 * @param endPaciente
	 * @param codigoCidadePadrao
	 * @return
	 */
	private String verificarDadosEndereco(AipEnderecosPacientes endPaciente,
			Integer codigoCidadePadrao) {
		String nomePosto = null;
		// Verifica se o endereço contem dados vindo do CEP, evitando
		// nullPointer
		if (endPaciente.getAipBairrosCepLogradouro() != null
				&& endPaciente.getAipBairrosCepLogradouro().getCepLogradouro() != null
				&& endPaciente.getAipBairrosCepLogradouro().getCepLogradouro()
						.getLogradouro() != null) {

			Integer codigoCidadePostoSaude = this
					.verificarCodigoCidadePostoSaude(endPaciente,
							codigoCidadePadrao);
			// Verifica se o código da cidade do parametro é igual ao código da
			// cidade do endereço do paciente
			if (codigoCidadePostoSaude != null) {
				nomePosto = this.buscarNomePostoSaude(endPaciente);
			}
		}
		return nomePosto;
	}

	/**
	 * Verifica se o código da cidade do endereço do paciente é compatível com o
	 * código da cidade padrão.
	 * 
	 * @param endPaciente
	 * @param codigoCidadePadrao
	 * @return
	 */
	private Integer verificarCodigoCidadePostoSaude(
			AipEnderecosPacientes endPaciente, Integer codigoCidadePadrao) {

		Integer codigoCidade = null;

		if (endPaciente.getAipBairrosCepLogradouro().getCepLogradouro()
				.getLogradouro().getAipCidade() != null) {
			codigoCidade = endPaciente.getAipBairrosCepLogradouro()
					.getCepLogradouro().getLogradouro().getAipCidade()
					.getCodigo();
		} else {
			codigoCidade = endPaciente.getAipCidade() == null ? null
					: endPaciente.getAipCidade().getCodigo();
		}

		// Verifica se o código da cidade do parametro é igual ao código da
		// cidade do endereço do paciente
		if (codigoCidade != null && codigoCidade.equals(codigoCidadePadrao)) {
			return codigoCidade;
		} else {
			return null;
		}
	}

	/**
	 * Método para buscar o nome do posto.
	 * 
	 * @param endPaciente
	 * @return
	 */
	private String buscarNomePostoSaude(AipEnderecosPacientes endPaciente) {
		String nomePosto = null;
		MamAreaAtuacao areaAtuacao = this.obterAreaAtuacao(endPaciente
				.getAipBairrosCepLogradouro().getCepLogradouro()
				.getLogradouro().getCodigo());

		// Se area de atuação não foi encontrada pelo código do logradouro,
		// tenta buscar através da descrição do logradouro
		if (areaAtuacao == null && endPaciente.getLogradouro() != null
				&& !"".equals(endPaciente.getLogradouro())) {
			// Retira a string "RUA" do nome do logradouro
			String logradouro = endPaciente.getLogradouro().replace("RUA", "")
					.trim();
			areaAtuacao = this.obterAreaAtuacaoPorDescricao(logradouro);
		}

		if (areaAtuacao != null) {
			// Verifica se numero do endereço do paciente é par ou impar
			if (endPaciente.getNroLogradouro() % 2 == 0) {
				// PARES
				nomePosto = this.obterNomePosto(areaAtuacao.getSeq(),
						endPaciente.getNroLogradouro(), 0);
			} else {
				// IMPARES
				nomePosto = this.obterNomePosto(areaAtuacao.getSeq(),
						endPaciente.getNroLogradouro(), 1);
			}
		}
		return nomePosto;
	}

	/**
	 * Método para obter a área de atuação com situação 'A' através de um código
	 * de logradouro. Se a busca retornar mais de um objeto, é retornado o
	 * primeiro.
	 * 
	 * @param codigoLogradouro
	 * @return
	 */
	private MamAreaAtuacao obterAreaAtuacao(Integer codigoLogradouro) {
		return this.getAmbulatorioFacade().obterAreaAtuacaoAtivaPorCodigoLogradouro(codigoLogradouro);
	}

	/**
	 * Método para obter uma área de atuação através do nome do logradouro.
	 * 
	 * @param logradouro
	 * @return
	 */
	private MamAreaAtuacao obterAreaAtuacaoPorDescricao(String logradouro) {
		return this.getAmbulatorioFacade().obterAreaAtuacaoAtivaPorNomeLogradouro(logradouro);
	}

	/**
	 * Método para buscar o nome do posto de saúde através do código da área de
	 * atuação e do número do logradouro.
	 * 
	 * @param codigoAreaAtuacao
	 * @param numeroLogradouro
	 * @param opcao
	 *            de busca - 0: busca números pares; 1: busca números ímpares
	 * @return
	 */
	private String obterNomePosto(Integer codigoAreaAtuacao,
			Integer numeroLogradouro, Integer opcao) {
		return this.getPrescricaoMedicaFacade().obterNomePosto(codigoAreaAtuacao, numeroLogradouro, opcao);
	}

	/**
	 * Método para furar o cache do Hibernate e buscar os dados do responsável
	 * do paciente através do seqInternacao.
	 * 
	 * @param seqInternacao
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public AinResponsaveisPaciente obterResponsavelPaciente(
			Integer seqInternacao) {
		
		List<Object> responsaveis = this.getAinResponsaveisPacienteDAO().obterResponsavelPaciente(seqInternacao);

		AinResponsaveisPaciente responsavel = null;
		if (responsaveis != null && responsaveis.size() > 0) {
			Object[] resultado = (Object[]) responsaveis.get(0);

			responsavel = new AinResponsaveisPaciente();
			responsavel.setNome(resultado[0] == null ? null
					: (String) resultado[0]);
			responsavel.setLogradouro(resultado[1] == null ? null
					: (String) resultado[1]);
			responsavel.setCidade(resultado[2] == null ? null
					: (String) resultado[2]);

			String siglaUF = resultado[3] == null ? null
					: (String) resultado[3];
			if (siglaUF != null) {
				responsavel.setUf(getCadastrosBasicosPacienteFacade().obterUF(siglaUF));
			}

			responsavel.setCep(resultado[4] == null ? null
					: (Integer) resultado[4]);
			responsavel.setFone(resultado[5] == null ? null
					: (Long) resultado[5]);
			responsavel.setTipoResponsabilidade(DominioTipoResponsabilidade.C);
		}

		return responsavel;
	}

	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade(){
		return cadastrosBasicosPacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected AinResponsaveisPacienteDAO getAinResponsaveisPacienteDAO(){
		return ainResponsaveisPacienteDAO;
	}
}
