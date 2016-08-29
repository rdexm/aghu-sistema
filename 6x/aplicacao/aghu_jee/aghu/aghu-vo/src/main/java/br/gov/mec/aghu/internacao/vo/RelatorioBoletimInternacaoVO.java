package br.gov.mec.aghu.internacao.vo;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class RelatorioBoletimInternacaoVO {

	// Valor impresso nas observações de acordo com algumas condições
	public static final String OBSERVACAO_SOLICITAR_DOCS = "*****       SOLICITAR DOCUMENTOS PARA RECADASTRO       *****";

	// Atributos para "Identificacao do Paciente" - Dados da internação
	private String pacNome = "";
	private String pacProntuario = "";
	private String pacEndereco = "";
	private String pacNumero = "";
	private String pacComplemento = "";
	private String pacTelefone = "";
	private String pacBairro = "";
	private String pacPosto = "";
	private String pacCidade = "";
	private String pacUf = "";
	private String pacCep = "";
	private String pacDataNascimento = "";
	private String pacIdade = "";
	private String pacSexo = "";
	private String pacCor = "";
	private String pacEstadoCivil = "";
	private String pacProfissao = "";
	private String pacNaturalidade = "";
	private String pacUfNascimento = "";
	private String pacNacionalidade = "";
	private String pacNomeMae = "";
	private String pacCodigoPaciente = "";

	// Atributos para "Identificação do Responsável"
	private String respNome = "";
	private String respEndereco = "";
	private String respCidade = "";
	private String respUf = "";
	private String respCep = "";
	private String respFone = "";

	// Atributos para "Equipe Médica"  - Dados da internação
	private String intDataInternacao = "";
	private String intCidPrincipal = "";
	private String intCidPrincipalDesc = "";
	private String intCidSecundario = "";
	private String intCidSecundarioDesc = "";
	private String intClinica = "";
	private String intDifClasse = "";
	private String intAcompanhante = "";
	private String intAndar = "";
	private String intAla = "";
	private String intQL = ""; // Quarto e Leito no mesmo campo
	private String intQuarto = "";
	private String intLeito = "";
	private String intConvenio = "";
	private String intUnidade = "";
	private String intAcomodacao = "";
	private String intEspecialidade = "";
	private String intCaraterInternacao = "";
	private String intProcedimento = "";
	private String intProcedimentoDesc = "";
	private String intTabela = "";
	private String intNroAtendimento = "";
	private String intQuantDiasFaturamento = "";
	private String intData = "";
	private String intEspecialidadeAtendimento = "";

	// Atributos para "Equipe Médica"
	private String emEquipe = "";
	private String emCrm = "";
	private String emCpf = "";
	private String emEspecialidadeChefeEquipe = ""; // concatena esp+medico
	private String emCrmChefeEquipe = "";
	private String emCpfChefeEquipe = "";
	private String lbEquipe = "";
	private String lbChefeEquipe = "";

	// Atributos para "Funcionário que efetuou a Internação"
	private String funcNome = "";
	private String funcCartaoPonto = "";

	// Atributo para "Observação"
	private String obsJustificativa = "";
	private String obsObservacao = "";

	private String lblUnidade;
	private String pacCartaoSus = "";

	/**
	 * Método para atribuir valores aos campos que aparecem na parte
	 * "Identificação do Paciente" no relatório.
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public void setIdentificacaoPaciente(AipPacientes paciente) {

		if (paciente != null) {
			this.setPacNome(paciente.getNome());
			this.setPacProntuario(formatarProntuario(ObjectUtils.toString(paciente.getProntuario())));

			if (paciente.getNroCartaoSaude() != null) {
				this.setPacCartaoSus(paciente.getNroCartaoSaude().toString());
			}

			// Endereço do Paciente (baseado nas regras da View
			// V_AIP_ENDERECO_PACIENTE)
			String endereco = "";
			String numero = "";
			String complemento = "";
			String bairro = "";
			String cidade = "";
			String uf = "";
			String cep = "";

			if (paciente.getEnderecos() != null && paciente.getEnderecos().size() > 0) {
				AipEnderecosPacientes enderecoPaciente = null;
				for (AipEnderecosPacientes endPaciente : paciente.getEnderecos()) {
					if (DominioSimNao.S.equals(endPaciente.getIndPadrao())) {
						enderecoPaciente = endPaciente;
						break;
					} else if (DominioTipoEndereco.R.equals(endPaciente.getTipoEndereco())) {
						enderecoPaciente = endPaciente;
						break;
					}
				}

				if (enderecoPaciente != null && enderecoPaciente.getAipBairrosCepLogradouro() != null) {
					// Endereço tem BairrosCepLogradouro (2º select da view)
					if (enderecoPaciente.getAipBairrosCepLogradouro().getCepLogradouro() != null) {

						AipCepLogradouros cepLogradouro = enderecoPaciente.getAipBairrosCepLogradouro().getCepLogradouro();
						if (cepLogradouro.getLogradouro() != null) {
							String tipoLogradouro = ObjectUtils.toString(cepLogradouro.getLogradouro().getAipTipoLogradouro()
									.getAbreviatura());
							endereco = tipoLogradouro + " " + ObjectUtils.toString(cepLogradouro.getLogradouro().getNome());
							cidade = ObjectUtils.toString(cepLogradouro.getLogradouro().getAipCidade().getNome());
							cep = ObjectUtils.toString(cepLogradouro.getId().getCep());
							uf = ObjectUtils.toString(cepLogradouro.getLogradouro().getAipCidade().getAipUf().getSigla());
						}

						if (enderecoPaciente.getAipBairrosCepLogradouro() != null) {
							bairro = ObjectUtils.toString(enderecoPaciente.getAipBairrosCepLogradouro().getAipBairro().getDescricao());
						}
					}
					numero = ObjectUtils.toString(enderecoPaciente.getNroLogradouro());
					complemento = ObjectUtils.toString(enderecoPaciente.getComplLogradouro());
				} else if (enderecoPaciente != null && enderecoPaciente.getAipBairrosCepLogradouro() == null
						&& enderecoPaciente.getAipCidade() != null) {
					// Endereço tem Cidade, mas não tem BairrosCepLogradouro (3º
					// select da view)
					endereco = ObjectUtils.toString(enderecoPaciente.getLogradouro());
					numero = ObjectUtils.toString(enderecoPaciente.getNroLogradouro());
					complemento = ObjectUtils.toString(enderecoPaciente.getComplLogradouro());
					bairro = ObjectUtils.toString(enderecoPaciente.getBairro());
					cidade = ObjectUtils.toString(enderecoPaciente.getAipCidade().getNome());

					cep = enderecoPaciente.getCep() == null ? ObjectUtils.toString(enderecoPaciente.getAipCidade().getCep()) : ObjectUtils
							.toString(enderecoPaciente.getCep());

					if (enderecoPaciente.getAipCidade().getAipUf() != null) {
						uf = ObjectUtils.toString(enderecoPaciente.getAipCidade().getAipUf().getSigla());
					}

				} else if (enderecoPaciente != null && enderecoPaciente.getAipBairrosCepLogradouro() == null
						&& enderecoPaciente.getAipCidade() == null) {
					// Endereço não tem BairrosCepLogradouro nem Cidade (1º
					// select da view)
					endereco = ObjectUtils.toString(enderecoPaciente.getLogradouro());
					numero = ObjectUtils.toString(enderecoPaciente.getNroLogradouro());
					complemento = ObjectUtils.toString(enderecoPaciente.getComplLogradouro());
					bairro = ObjectUtils.toString(enderecoPaciente.getBairro());
					cidade = ObjectUtils.toString(enderecoPaciente.getCidade());
					cep = ObjectUtils.toString(enderecoPaciente.getCep());
					uf = ObjectUtils.toString(enderecoPaciente.getAipUf() == null ? "" : ObjectUtils.toString(enderecoPaciente.getAipUf()
							.getSigla()));
				}
			}

			this.setPacEndereco(endereco);
			this.setPacNumero(numero);
			this.setPacComplemento(complemento);
			this.setPacBairro(bairro);
			this.setPacCidade(cidade);
			this.setPacUf(uf);
			this.setPacCep(formatarCep(cep));

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			this.setPacDataNascimento(paciente.getDtNascimento() == null ? ""
					: ObjectUtils.toString(sdf.format(paciente.getDtNascimento())));
			this.setPacIdade(ObjectUtils.toString(paciente.getIdade()));
			this.setPacSexo(ObjectUtils.toString(paciente.getSexo()));
			this.setPacCor(ObjectUtils.toString(paciente.getCor()));
			this.setPacEstadoCivil(ObjectUtils.toString(paciente.getEstadoCivil()));

			// Profissão do Paciente
			AipOcupacoes ocupacao = paciente.getAipOcupacoes();
			if (ocupacao != null) {
				this.setPacProfissao(ObjectUtils.toString(ocupacao.getDescricao()));
			}

			this.setPacNaturalidade(paciente.getAipCidades() == null ? "" : ObjectUtils.toString(paciente.getAipCidades().getNome()));

			// Cidade do Paciente
			AipCidades cidadePaciente = paciente.getAipCidades();
			if (cidadePaciente != null && cidadePaciente.getAipUf() != null) {
				this.setPacUfNascimento(ObjectUtils.toString(cidadePaciente.getAipUf().getSigla()));
			}

			this.setPacNacionalidade(ObjectUtils.toString(paciente.getAipNacionalidades() == null ? "" : paciente.getAipNacionalidades()
					.getDescricao()));
			this.setPacNomeMae(ObjectUtils.toString(paciente.getNomeMae()));
			this.setPacCodigoPaciente(ObjectUtils.toString(paciente.getCodigo()));
		}
	}

	/**
	 * Método para atribuir valores aos campos que aparecem na parte
	 * "Identificação do Responsável" no relatório.
	 */
	public void setIdentificacaoResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente, AghResponsavel responsavelPac) {
		if (responsavelPaciente != null) {
			this.setRespNome(ObjectUtils.toString(responsavelPaciente.getNome()));
			this.setRespEndereco(ObjectUtils.toString(responsavelPaciente.getLogradouro()));
			this.setRespCidade(ObjectUtils.toString(responsavelPaciente.getCidade()));
			this.setRespUf(responsavelPaciente.getUf() == null ? "" : ObjectUtils.toString(responsavelPaciente.getUf().getSigla()));
			this.setRespCep(this.formatarCep(ObjectUtils.toString(responsavelPaciente.getCep())));

			// Fone do Responsável
			StringBuffer fone = new StringBuffer(ObjectUtils.toString(responsavelPaciente.getDddFone()));
			if (fone.length() > 0) {
				fone = new StringBuffer(StringUtils.leftPad(fone.toString(), 3, "0")).append(' ');
			}
			fone.append(ObjectUtils.toString(responsavelPaciente.getFone()));
			this.setRespFone(fone.toString());
		}
		else {
			if(responsavelPac.getAipPaciente()!=null) {
				AipPacientes pacienteResponsavel = responsavelPac.getAipPaciente();
				AipEnderecosPacientes enderecoPaciente = responsavelPac.getAipPaciente().getEnderecoPadrao();
				
				this.setRespNome(ObjectUtils.toString(pacienteResponsavel.getNome()));
				
				if(enderecoPaciente!=null) {
					StringBuffer endereco = new StringBuffer(ObjectUtils.toString(enderecoPaciente.getLogradouroEndereco()));
					if(enderecoPaciente.getNroLogradouro() != null) {
						endereco.append(ObjectUtils.toString(", ")+ObjectUtils.toString(enderecoPaciente.getNroLogradouro()));
					}
					if(enderecoPaciente.getBairroEndereco() != null) {
						endereco.append(ObjectUtils.toString(" - ")+ObjectUtils.toString(enderecoPaciente.getBairroEndereco()));
					}
					this.setRespEndereco(endereco.toString());
					this.setRespCidade(ObjectUtils.toString(enderecoPaciente.getCidadeEndereco()));
					this.setRespUf(enderecoPaciente.getUfEndereco());
					if(enderecoPaciente.getCepEndereco() != null) {
						this.setRespCep(this.formatarCep(ObjectUtils.toString(enderecoPaciente.getCepEndereco())));
					}
				}

				// Fone do Responsável
				StringBuffer fone = new StringBuffer(ObjectUtils.toString(pacienteResponsavel.getDddFoneResidencial()));
				if (fone.length() > 0) {
					fone = new StringBuffer(StringUtils.leftPad(fone.toString(), 3, "0")).append(' ');
				}
				fone.append(ObjectUtils.toString(pacienteResponsavel.getFoneResidencial()));
				this.setRespFone(fone.toString());
				
			}
			else {
				this.setRespNome(ObjectUtils.toString(responsavelPac.getNome()));
				if(responsavelPac.getAipBairrosCepLogradouro()!=null) {
					StringBuffer endereco = new StringBuffer(ObjectUtils.toString(responsavelPac.getAipBairrosCepLogradouro().getAipLogradouro().getAipTipoLogradouro().getDescricao()));
					endereco.append(ObjectUtils.toString(" ")+ObjectUtils.toString(responsavelPac.getAipBairrosCepLogradouro().getAipLogradouro().getNome()));
					if(responsavelPac.getNroLogradouro() != null) {
						endereco.append(ObjectUtils.toString(", ")+ObjectUtils.toString(responsavelPac.getNroLogradouro()));
					}
					endereco.append(ObjectUtils.toString(" - ")+ObjectUtils.toString(responsavelPac.getAipBairrosCepLogradouro().getAipBairro().getDescricao()));
					this.setRespEndereco(endereco.toString());
					this.setRespCidade(ObjectUtils.toString(responsavelPac.getAipBairrosCepLogradouro().getAipLogradouro().getAipCidade().getNome()));
					this.setRespUf(responsavelPac.getAipBairrosCepLogradouro().getAipLogradouro().getAipCidade().getAipUf().getSigla() == null ? "" : ObjectUtils.toString(responsavelPac.getAipBairrosCepLogradouro().getAipLogradouro().getAipCidade().getAipUf().getSigla()));
					this.setRespCep(ObjectUtils.toString(responsavelPac.getAipBairrosCepLogradouro().getCepFormatado()));
				}

				// Fone do Responsável
				StringBuffer fone = new StringBuffer(ObjectUtils.toString(responsavelPac.getDddFone()));
				if (fone.length() > 0) {
					fone = new StringBuffer(StringUtils.leftPad(fone.toString(), 3, "0")).append(' ');
				}
				fone.append(ObjectUtils.toString(responsavelPac.getFone()));
				this.setRespFone(fone.toString());
			}
		}
	}

	/**
	 * Método para atribuir valores aos campos que aparecem na parte
	 * "Dados da Internacao" no relatório.
	 */
	
	public void setRelatorioInternacao(AinInternacao internacao, AinMovimentosInternacao movimentoInternacao,
			FatItensProcedHospitalar procedimentoHospitalar) {

		if (internacao != null) {

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			populateMovimentoInternacao(internacao, movimentoInternacao);
			populateProcedimentoHospitalar(procedimentoHospitalar);
			populateAtendimentoUrgencia(internacao.getAtendimentoUrgencia(), sdf);
			populateCid(internacao.getCidsInternacao());
			populateConvenioPlanoSaude(internacao.getConvenioSaudePlano());
			populateInternacao(internacao, sdf);
		}

	}
		
	private void populateInternacao(AinInternacao internacao, SimpleDateFormat sdf) {
		if (internacao.getAtendimentoUrgencia() != null && internacao.getAtendimentoUrgencia().getDtAtendimento() != null) {
			this.setIntDataInternacao(ObjectUtils.toString(sdf.format(internacao.getAtendimentoUrgencia().getDtAtendimento())));
		} else {
			this.setIntDataInternacao(ObjectUtils.toString(sdf.format(internacao.getDthrInternacao())));
		}		

		this.setIntCaraterInternacao(internacao.getTipoCaracterInternacao() == null ? "" : internacao.getTipoCaracterInternacao().getDescricao());
		
		String difClasse = "";

		if (internacao.getIndDifClasse() != null) {
			difClasse = internacao.getIndDifClasse() == true ? "S" : "N";
		}
		this.setIntDifClasse(difClasse);
		this.setIntAcompanhante(internacao.getAcompanhantesInternacao() == null || internacao.getAcompanhantesInternacao().size() == 0 ? "N" : "S");
	}
	
	private void populateConvenioPlanoSaude(FatConvenioSaudePlano convenioSaudePlano) {
		if (convenioSaudePlano != null) {
			StringBuffer convenio = new StringBuffer(
					ObjectUtils.toString(convenioSaudePlano.getId().getCnvCodigo())).append('/').append(ObjectUtils.toString(convenioSaudePlano.getId().getSeq())).append("  ")
					.append(ObjectUtils.toString(convenioSaudePlano.getConvenioSaude().getDescricao())).append(" - ").append(ObjectUtils.toString(convenioSaudePlano.getDescricao()));

			this.setIntConvenio(convenio.toString());

		}
	}
	
	private void populateCid(Set<AinCidsInternacao> collectionSetCids) {
		if (collectionSetCids != null && collectionSetCids.size() > 0) {
			for (AinCidsInternacao cidInternacao : collectionSetCids) {
				if (DominioPrioridadeCid.P.equals(cidInternacao.getPrioridadeCid())) {
					this.setIntCidPrincipal(ObjectUtils.toString(cidInternacao.getCid().getCodigo()));
					this.setIntCidPrincipalDesc(cidInternacao.getCid().getDescricao() == null ? "" : ObjectUtils
							.toString(cidInternacao.getCid().getDescricao()));
				} else if (DominioPrioridadeCid.S.equals(cidInternacao.getPrioridadeCid())) {
					this.setIntCidSecundario(ObjectUtils.toString(cidInternacao.getCid().getCodigo()));
					this.setIntCidSecundarioDesc(cidInternacao.getCid().getDescricao() == null ? "" : ObjectUtils
							.toString(cidInternacao.getCid().getDescricao()));
				}
			}
		}
	}
	
	private void populateAtendimentoUrgencia(AinAtendimentosUrgencia atendimentoUrgencia, SimpleDateFormat sdf) {
		if (atendimentoUrgencia != null) {
			Integer numeroConsulta = null;
			if (atendimentoUrgencia.getConsulta() != null) {
				numeroConsulta = atendimentoUrgencia.getConsulta().getNumero();
			}
			this.setIntNroAtendimento(ObjectUtils.toString(numeroConsulta));

			if (atendimentoUrgencia.getConsulta() != null) {
				if (atendimentoUrgencia.getDtAtendimento() != null) {
					this.setIntData("Data: "
							+ ObjectUtils.toString(sdf.format(atendimentoUrgencia.getDtAtendimento())));
				}
				if (atendimentoUrgencia.getEspecialidade() != null) {
					this.setIntEspecialidadeAtendimento("Especialidade: "
							+ ObjectUtils.toString(atendimentoUrgencia.getEspecialidade().getSigla()));
				}
			}
		}
	}
	
	private void populateProcedimentoHospitalar(FatItensProcedHospitalar procedimentoHospitalar) {
		this.setIntProcedimento(ObjectUtils.toString(procedimentoHospitalar.getCodTabela()));
		this.setIntProcedimentoDesc(procedimentoHospitalar.getDescricao() == null ? "" : ObjectUtils
				.toString(procedimentoHospitalar.getDescricao()));
		this.setIntTabela(procedimentoHospitalar.getProcedimentoHospitalar() == null ? "" : ObjectUtils
				.toString(procedimentoHospitalar.getProcedimentoHospitalar().getDescricao()));

		this.setIntQuantDiasFaturamento(procedimentoHospitalar.getQuantDiasFaturamento() == null ? "" : ObjectUtils
				.toString(procedimentoHospitalar.getQuantDiasFaturamento() + " Dia(s)"));
	}
	
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private void populateMovimentoInternacao(AinInternacao internacao, AinMovimentosInternacao movimentoInternacao) {
		if (movimentoInternacao != null && movimentoInternacao.getUnidadeFuncional() != null) {
			AghUnidadesFuncionais unidadeFuncional = movimentoInternacao.getUnidadeFuncional();

			this.setIntClinica(movimentoInternacao == null || movimentoInternacao.getQuarto() == null
					|| movimentoInternacao.getQuarto().getClinica() == null ? "" : ObjectUtils.toString(movimentoInternacao
					.getQuarto().getClinica().getDescricao()));

			this.setIntAndar(ObjectUtils.toString(unidadeFuncional.getAndar()));
			this.setIntAla(ObjectUtils.toString(unidadeFuncional.getIndAla()));

			this.setIntLeito(movimentoInternacao.getLeito() == null || movimentoInternacao.getLeito().getLeito() == null ? ""
					: ObjectUtils.toString(movimentoInternacao.getLeito().getLeito()));

			this.setIntQuarto(movimentoInternacao.getQuarto() == null
					|| movimentoInternacao.getQuarto().getDescricao() == null ? "" : ObjectUtils.toString(movimentoInternacao
					.getQuarto().getDescricao()));

			this.setIntUnidade(unidadeFuncional.getDescricao());
			this.setIntEspecialidade(movimentoInternacao.getEspecialidade() == null ? "" : ObjectUtils.toString(internacao
					.getEspecialidade().getNomeEspecialidade()));
			if (movimentoInternacao.getQuarto() != null && movimentoInternacao.getQuarto().getAcomodacao() != null) {
				this.setIntAcomodacao(ObjectUtils.toString(movimentoInternacao.getQuarto().getAcomodacao().getDescricao()));
			}
		}
	}

	/**
	 * Método para atribuir valores aos campos que aparecem na parte
	 * "Equipe Médica" no relatório.
	 */
	public void setEquipeMedica(AinInternacao internacao) {

		if (internacao != null && internacao.getServidorProfessor() != null) {
			String nomeMedico = internacao.getServidorProfessor().getPessoaFisica() == null ? "" : ObjectUtils.toString(internacao
					.getServidorProfessor().getPessoaFisica().getNome());
			this.setEmEquipe(nomeMedico);
			this.setEmCpf(ObjectUtils.toString(internacao.getServidorProfessor().getPessoaFisica().getCpf()));

			String crm = "";
			// Busca CRM do médico responsável
			if (internacao.getServidorProfessor().getPessoaFisica() != null
					&& internacao.getServidorProfessor().getPessoaFisica().getQualificacoes() != null) {

				final String conselhoMedicina = ConselhoRegionalMedicinaEnum.getValores();
				final String conselhoOdontologia = ConselhoRegionalOdontologiaEnum.getValores();

				String conselhoProfissional = "";

				// Encontrará somente um registro válido, por isso foi colocado
				// o "break"
				for (RapQualificacao qualificacao : internacao.getServidorProfessor().getPessoaFisica().getQualificacoes()) {

					if (qualificacao.getTipoQualificacao() != null && qualificacao.getTipoQualificacao().getConselhoProfissional() != null) {

						conselhoProfissional = qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla();

						// Verifica se o conselho do profissional (CRM, CRO etc)
						// é equivalente a da qualificação
						if (conselhoMedicina.indexOf("'" + conselhoProfissional + "'") > -1
								|| conselhoOdontologia.indexOf("'" + conselhoProfissional + "'") > -1) {
							crm = ObjectUtils.toString(qualificacao.getNroRegConselho());
							break;
						}
					}
				}

				this.setEmCrm(crm);
			}

			String especialidadeInternacao = "";
			String cpfMedicoEspecialidade = "";
			String crmMedicoEspecialidade = "";
			String medicoChefeEspecialidade = "";

			if (internacao.getEspecialidade() != null) {
				if (internacao.getEspecialidade().getServidorChefe() != null
						&& internacao.getEspecialidade().getServidorChefe().getPessoaFisica() != null) {
					medicoChefeEspecialidade = internacao.getEspecialidade().getServidorChefe().getPessoaFisica().getNome();
					cpfMedicoEspecialidade = ObjectUtils.toString(internacao.getEspecialidade().getServidorChefe().getPessoaFisica()
							.getCpf());

					// Busca CRM do médico chefe especialidade
					if (internacao.getEspecialidade().getServidorChefe().getPessoaFisica().getQualificacoes() != null) {
						final String conselhoMedicina = ConselhoRegionalMedicinaEnum.getValores();
						final String conselhoOdontologia = ConselhoRegionalOdontologiaEnum.getValores();

						String conselhoProfissional = "";

						// Encontrará somente um registro válido, por isso foi
						// colocado
						// o "break"
						for (RapQualificacao qualificacao : internacao.getEspecialidade().getServidorChefe().getPessoaFisica()
								.getQualificacoes()) {

							if (qualificacao.getTipoQualificacao() != null
									&& qualificacao.getTipoQualificacao().getConselhoProfissional() != null) {
								conselhoProfissional = qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla();

								// Verifica se o conselho do profissional (CRM,
								// CRO etc) é equivalente a da qualificação

								if (conselhoMedicina.indexOf("'" + conselhoProfissional + "'") > -1
										|| conselhoOdontologia.indexOf("'" + conselhoProfissional + "'") > -1) {
									crmMedicoEspecialidade = ObjectUtils.toString(qualificacao.getNroRegConselho());
									break;
								}
							}
						}
					}
				}

				especialidadeInternacao = ObjectUtils.toString(internacao.getEspecialidade().getNomeEspecialidade());
			}
			this.setEmEspecialidadeChefeEquipe(especialidadeInternacao + " : " + medicoChefeEspecialidade);
			this.setEmCpfChefeEquipe(cpfMedicoEspecialidade);
			this.setEmCrmChefeEquipe(crmMedicoEspecialidade);
		}
	}

	/**
	 * Método para atribuir valores aos campos que aparecem na parte
	 * "Funcionario que efetuou a Internação" no relatório.
	 */
	public void setFuncionarioInternacao(AinMovimentosInternacao movimentoInternacao) {

		if (movimentoInternacao.getServidorGerado() != null) {
			RapServidores servidor = movimentoInternacao.getServidorGerado();
			this.setFuncNome(servidor.getPessoaFisica() == null ? "" : ObjectUtils.toString(servidor.getPessoaFisica().getNome()));

			StringBuffer cartaoPonto = new StringBuffer(
					ObjectUtils.toString(movimentoInternacao.getServidorGerado().getId().getVinCodigo())).append(' ').append(
					ObjectUtils.toString(movimentoInternacao.getServidorGerado().getId().getMatricula()));
			this.setFuncCartaoPonto(cartaoPonto.toString());
		}
	}

	/**
	 * Método para atribuir valores aos campos que aparecem na parte
	 * "Observação" no relatório.
	 */
	public void setObservacao(AinInternacao internacao) {
		if (internacao.getJustificativaAltDel() != null && !"".equals(internacao.getJustificativaAltDel())) {
			// Seta a justificativa e seu label no relatório
			this.setObsJustificativa(ObjectUtils.toString(internacao.getJustificativaAltDel()));
		}

		if (internacao.getPaciente() != null && DominioTipoProntuario.R.equals(internacao.getPaciente().getPrntAtivo())) {
			this.setObsObservacao(RelatorioBoletimInternacaoVO.OBSERVACAO_SOLICITAR_DOCS);
		}
	}

	/**
	 * Método para formatar prontuário do paciente com máscara 0000000/0.
	 * 
	 * @param String
	 *            prontuário
	 * @return prontuário formatado
	 */
	private String formatarProntuario(String prontuario) {
		if (prontuario == null || "".equals(prontuario)) {
			return "";
		} else {
			String prontuarioFormatado = prontuario;
			prontuarioFormatado = StringUtils.leftPad(prontuario, 8, "0");
			prontuarioFormatado = prontuarioFormatado.substring(0, prontuarioFormatado.length() - 1) + "/"
					+ prontuarioFormatado.charAt(prontuarioFormatado.length() - 1);

			return prontuarioFormatado;
		}
	}

	/**
	 * Método para formatar o CEP com máscara 00.000-000 Máscara espécífica para
	 * relatórios.
	 * 
	 * @param String
	 *            cep
	 * @return Cep formatado
	 */
	private String formatarCep(String cep) {
		if (cep == null || "".equals(cep) || "0".equals(cep)) {
			return "";
		} else {
			String cepFormatado = cep;
			try {
				cepFormatado = cep.substring(0, cep.length() - 6) + "." + cep.substring(2, cep.length() - 3) + "-"
						+ cep.substring(cep.length() - 3, cep.length());
			} catch (Exception e) {
				cepFormatado = "";
			}
			return cepFormatado;
		}
	}

	// GETTERS e SETTERS

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public String getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(String pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getPacEndereco() {
		return pacEndereco;
	}

	public void setPacEndereco(String pacEndereco) {
		this.pacEndereco = pacEndereco;
	}

	public String getPacNumero() {
		return pacNumero;
	}

	public void setPacNumero(String pacNumero) {
		this.pacNumero = pacNumero;
	}

	public String getPacComplemento() {
		return pacComplemento;
	}

	public void setPacComplemento(String pacComplemento) {
		this.pacComplemento = pacComplemento;
	}

	public String getPacTelefone() {
		return pacTelefone;
	}

	public void setPacTelefone(String pacTelefone) {
		this.pacTelefone = pacTelefone;
	}

	public String getPacBairro() {
		return pacBairro;
	}

	public void setPacBairro(String pacBairro) {
		this.pacBairro = pacBairro;
	}

	public String getPacPosto() {
		return pacPosto;
	}

	public void setPacPosto(String pacPosto) {
		this.pacPosto = pacPosto;
	}

	public String getPacCidade() {
		return pacCidade;
	}

	public void setPacCidade(String pacCidade) {
		this.pacCidade = pacCidade;
	}

	public String getPacUf() {
		return pacUf;
	}

	public void setPacUf(String pacUf) {
		this.pacUf = pacUf;
	}

	public String getPacCep() {
		return pacCep;
	}

	public void setPacCep(String pacCep) {
		this.pacCep = pacCep;
	}

	public String getPacDataNascimento() {
		return pacDataNascimento;
	}

	public void setPacDataNascimento(String pacDataNascimento) {
		this.pacDataNascimento = pacDataNascimento;
	}
	
	public String getPacIdade() {
		return pacIdade;
	}

	public void setPacIdade(String pacIdade) {
		this.pacIdade = pacIdade;
	}


	public String getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(String pacSexo) {
		this.pacSexo = pacSexo;
	}

	public String getPacCor() {
		return pacCor;
	}

	public void setPacCor(String pacCor) {
		this.pacCor = pacCor;
	}

	public String getPacEstadoCivil() {
		return pacEstadoCivil;
	}

	public void setPacEstadoCivil(String pacEstadoCivil) {
		this.pacEstadoCivil = pacEstadoCivil;
	}

	public String getPacProfissao() {
		return pacProfissao;
	}

	public void setPacProfissao(String pacProfissao) {
		this.pacProfissao = pacProfissao;
	}

	public String getPacNaturalidade() {
		return pacNaturalidade;
	}

	public void setPacNaturalidade(String pacNaturalidade) {
		this.pacNaturalidade = pacNaturalidade;
	}

	public String getPacUfNascimento() {
		return pacUfNascimento;
	}

	public void setPacUfNascimento(String pacUfNascimento) {
		this.pacUfNascimento = pacUfNascimento;
	}

	public String getPacNacionalidade() {
		return pacNacionalidade;
	}

	public void setPacNacionalidade(String pacNacionalidade) {
		this.pacNacionalidade = pacNacionalidade;
	}

	public String getPacNomeMae() {
		return pacNomeMae;
	}

	public void setPacNomeMae(String pacNomeMae) {
		this.pacNomeMae = pacNomeMae;
	}

	public String getPacCodigoPaciente() {
		return pacCodigoPaciente;
	}

	public void setPacCodigoPaciente(String pacCodigoPaciente) {
		this.pacCodigoPaciente = pacCodigoPaciente;
	}

	public String getRespNome() {
		return respNome;
	}

	public void setRespNome(String respNome) {
		this.respNome = respNome;
	}

	public String getRespEndereco() {
		return respEndereco;
	}

	public void setRespEndereco(String respEndereco) {
		this.respEndereco = respEndereco;
	}

	public String getRespCidade() {
		return respCidade;
	}

	public void setRespCidade(String respCidade) {
		this.respCidade = respCidade;
	}

	public String getRespUf() {
		return respUf;
	}

	public void setRespUf(String respUf) {
		this.respUf = respUf;
	}

	public String getRespCep() {
		return respCep;
	}

	public void setRespCep(String respCep) {
		this.respCep = respCep;
	}

	public String getRespFone() {
		return respFone;
	}

	public void setRespFone(String respFone) {
		this.respFone = respFone;
	}

	public String getIntDataInternacao() {
		return intDataInternacao;
	}

	public void setIntDataInternacao(String intDataInternacao) {
		this.intDataInternacao = intDataInternacao;
	}

	public String getIntCidPrincipal() {
		return intCidPrincipal;
	}

	public void setIntCidPrincipal(String intCidPrincipal) {
		this.intCidPrincipal = intCidPrincipal;
	}

	public String getIntCidSecundario() {
		return intCidSecundario;
	}

	public void setIntCidSecundario(String intCidSecundario) {
		this.intCidSecundario = intCidSecundario;
	}
	
	public String getIntCidPrincipalDesc() {
		return intCidPrincipalDesc;
	}

	public void setIntCidPrincipalDesc(String intCidPrincipalDesc) {
		this.intCidPrincipalDesc = intCidPrincipalDesc;
	}

	public String getIntCidSecundarioDesc() {
		return intCidSecundarioDesc;
	}

	public void setIntCidSecundarioDesc(String intCidSecundarioDesc) {
		this.intCidSecundarioDesc = intCidSecundarioDesc;
	}

	public String getIntClinica() {
		return intClinica;
	}

	public void setIntClinica(String intClinica) {
		this.intClinica = intClinica;
	}

	public String getIntDifClasse() {
		return intDifClasse;
	}

	public void setIntDifClasse(String intDifClasse) {
		this.intDifClasse = intDifClasse;
	}

	public String getIntAcompanhante() {
		return intAcompanhante;
	}

	public void setIntAcompanhante(String intAcompanhante) {
		this.intAcompanhante = intAcompanhante;
	}

	public String getIntAndar() {
		return intAndar;
	}

	public void setIntAndar(String intAndar) {
		this.intAndar = intAndar;
	}

	public String getIntAla() {
		return intAla;
	}

	public void setIntAla(String intAla) {
		this.intAla = intAla;
	}
	
	public String getIntQuarto() {
		return intQuarto;
	}

	public void setIntQuarto(String intQuarto) {
		this.intQuarto = intQuarto;
	}

	public String getIntLeito() {
		return intLeito;
	}

	public void setIntLeito(String intLeito) {
		this.intLeito = intLeito;
	}

	public String getIntQL() {
		return intQL;
	}

	public void setIntQL(String intQL) {
		this.intQL = intQL;
	}

	public String getIntConvenio() {
		return intConvenio;
	}

	public void setIntConvenio(String intConvenio) {
		this.intConvenio = intConvenio;
	}

	public String getIntUnidade() {
		return intUnidade;
	}

	public void setIntUnidade(String intUnidade) {
		this.intUnidade = intUnidade;
	}

	public String getIntAcomodacao() {
		return intAcomodacao;
	}

	public void setIntAcomodacao(String intAcomodacao) {
		this.intAcomodacao = intAcomodacao;
	}

	public String getIntEspecialidade() {
		return intEspecialidade;
	}

	public void setIntEspecialidade(String intEspecialidade) {
		this.intEspecialidade = intEspecialidade;
	}

	public String getIntCaraterInternacao() {
		return intCaraterInternacao;
	}

	public void setIntCaraterInternacao(String intCaraterInternacao) {
		this.intCaraterInternacao = intCaraterInternacao;
	}

	public String getIntProcedimento() {
		return intProcedimento;
	}

	public void setIntProcedimento(String intProcedimento) {
		this.intProcedimento = intProcedimento;
	}
	
	public String getIntProcedimentoDesc() {
		return intProcedimentoDesc;
	}

	public void setIntProcedimentoDesc(String intProcedimentoDesc) {
		this.intProcedimentoDesc = intProcedimentoDesc;
	}


	public String getIntTabela() {
		return intTabela;
	}

	public void setIntTabela(String intTabela) {
		this.intTabela = intTabela;
	}
	
	public String getIntQuantDiasFaturamento() {
		return intQuantDiasFaturamento;

	}

	public void setIntQuantDiasFaturamento(String intQuantDiasFaturamento) {
		this.intQuantDiasFaturamento = intQuantDiasFaturamento;
	}


	public String getIntNroAtendimento() {
		return intNroAtendimento;
	}

	public void setIntNroAtendimento(String intNroAtendimento) {
		this.intNroAtendimento = intNroAtendimento;
	}

	public String getIntData() {
		return intData;
	}

	public void setIntData(String intData) {
		this.intData = intData;
	}

	public String getIntEspecialidadeAtendimento() {
		return intEspecialidadeAtendimento;
	}

	public void setIntEspecialidadeAtendimento(String intEspecialidadeAtendimento) {
		this.intEspecialidadeAtendimento = intEspecialidadeAtendimento;
	}

	public String getEmEquipe() {
		return emEquipe;
	}

	public void setEmEquipe(String emEquipe) {
		this.emEquipe = emEquipe;
	}

	public String getEmCrm() {
		return emCrm;
	}

	public void setEmCrm(String emCrm) {
		this.emCrm = emCrm;
	}

	public String getEmCpf() {
		return emCpf;
	}

	public void setEmCpf(String emCpf) {
		this.emCpf = emCpf;
	}

	public String getEmEspecialidadeChefeEquipe() {
		return emEspecialidadeChefeEquipe;
	}

	public void setEmEspecialidadeChefeEquipe(String emEspecialidadeChefeEquipe) {
		this.emEspecialidadeChefeEquipe = emEspecialidadeChefeEquipe;
	}

	public String getEmCrmChefeEquipe() {
		return emCrmChefeEquipe;
	}

	public void setEmCrmChefeEquipe(String emCrmChefeEquipe) {
		this.emCrmChefeEquipe = emCrmChefeEquipe;
	}

	public String getEmCpfChefeEquipe() {
		return emCpfChefeEquipe;
	}

	public void setEmCpfChefeEquipe(String emCpfChefeEquipe) {
		this.emCpfChefeEquipe = emCpfChefeEquipe;
	}

	public String getFuncNome() {
		return funcNome;
	}

	public void setFuncNome(String funcNome) {
		this.funcNome = funcNome;
	}

	public String getFuncCartaoPonto() {
		return funcCartaoPonto;
	}

	public void setFuncCartaoPonto(String funcCartaoPonto) {
		this.funcCartaoPonto = funcCartaoPonto;
	}

	public String getObsJustificativa() {
		return obsJustificativa;
	}

	public void setObsJustificativa(String obsJustificativa) {
		this.obsJustificativa = obsJustificativa;
	}

	public String getObsObservacao() {
		return obsObservacao;
	}

	public void setObsObservacao(String obsObservacao) {
		this.obsObservacao = obsObservacao;
	}

	public String getLbEquipe() {
		return lbEquipe;
	}

	public void setLbEquipe(String lbEquipe) {
		this.lbEquipe = lbEquipe;
	}

	public String getLbChefeEquipe() {
		return lbChefeEquipe;
	}

	public void setLbChefeEquipe(String lbChefeEquipe) {
		this.lbChefeEquipe = lbChefeEquipe;
	}

	public String getPacCartaoSus() {
		return pacCartaoSus;
	}

	public void setPacCartaoSus(String pacCartaoSus) {
		this.pacCartaoSus = pacCartaoSus;
	}

	public String getLblUnidade() {
		return lblUnidade;
	}

	public void setLblUnidade(String lblUnidade) {
		this.lblUnidade = lblUnidade;
	}

	@Override
	public String toString() {
		return "RelatorioBoletimInternacaoVO [emCpf=" + emCpf + ", emCpfChefeEquipe=" + emCpfChefeEquipe + ", emCrm=" + emCrm
				+ ", emCrmChefeEquipe=" + emCrmChefeEquipe + ", emEquipe=" + emEquipe + ", emEspecialidadeChefeEquipe="
				+ emEspecialidadeChefeEquipe + ", funcCartaoPonto=" + funcCartaoPonto + ", funcNome=" + funcNome
				+ ", intAcomodacao=" + intAcomodacao + ", intAcompanhante=" + intAcompanhante + ", intAla=" + intAla
				+ ", intAndar=" + intAndar + ", intCaraterInternacao=" + intCaraterInternacao + ", intCidPrincipal="
				+ intCidPrincipal + ", intCidPrincipalDesc=" + intCidPrincipalDesc + ", intCidSecundarioDesc="
				+ intCidSecundarioDesc + ", intCidSecundario=" + intCidSecundario + ", intClinica=" + intClinica
				+ ", intConvenio=" + intConvenio + ", intDataInternacao=" + intDataInternacao + ", intDifClasse=" + intDifClasse
				+ ", intEspecialidade=" + intEspecialidade + ", intNroAtendimento=" + intNroAtendimento + ", intProcedimento="
				+ intProcedimento + ", intProcedimentoDesc" + intProcedimentoDesc + ", intQL=" + intQL + ", intTabela="
				+ intTabela + ", intUnidade=" + intUnidade + ", obsJustificativa=" + obsJustificativa + ", obsObservacao="
				+ obsObservacao + ", pacBairro=" + pacBairro + ", pacCep=" + pacCep + ", pacCidade=" + pacCidade
				+ ", pacCodigoPaciente=" + pacCodigoPaciente + ", pacComplemento=" + pacComplemento + ", pacCor=" + pacCor
				+ ", pacDataNascimento=" + pacDataNascimento + ", pacEndereco=" + pacEndereco + ", pacEstadoCivil="
				+ pacEstadoCivil + ", pacNacionalidade=" + pacNacionalidade + ", pacNaturalidade=" + pacNaturalidade
				+ ", pacNome=" + pacNome + ", pacNomeMae=" + pacNomeMae + ", pacNumero=" + pacNumero + ", pacProfissao="
				+ pacProfissao + ", pacProntuario=" + pacProntuario + ", pacSexo=" + pacSexo + ", pacTelefone=" + pacTelefone
				+ ", pacUf=" + pacUf + ", pacUfNascimento=" + pacUfNascimento + ", respCep=" + respCep + ", respCidade="
				+ respCidade + ", respEndereco=" + respEndereco + ", respFone=" + respFone + ", respNome=" + respNome
				+ ", respUf=" + respUf + ", lbEquipe=" + lbEquipe + "lbChefeEquipe=" + lbChefeEquipe + "pacCartaoSus="
				+ pacCartaoSus + "]";

	}

}
