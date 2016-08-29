package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimentoId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

/**
 * 
 * @author rhrosa
 * 
 */
@Stateless
public class ManterItensModeloBasicoON extends BaseBusiness {


private static final String _PONTO_VIRGULA_ = " ; ";

@EJB
private ManterCuidadosModeloBasicoON manterCuidadosModeloBasicoON;

@EJB
private ManterDietasModeloBasicoON manterDietasModeloBasicoON;

private static final Log LOG = LogFactory.getLog(ManterItensModeloBasicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;

@Inject
private MpmModeloBasicoCuidadoDAO mpmModeloBasicoCuidadoDAO;

@Inject
private MpmModeloBasicoModoUsoProcedimentoDAO mpmModeloBasicoModoUsoProcedimentoDAO;

@Inject
private MpmModeloBasicoPrescricaoDAO mpmModeloBasicoPrescricaoDAO;

@Inject
private MpmModeloBasicoProcedimentoDAO mpmModeloBasicoProcedimentoDAO;

@Inject
private MpmItemModeloBasicoMedicamentoDAO mpmItemModeloBasicoMedicamentoDAO;

@Inject
private MpmModeloBasicoDietaDAO mpmModeloBasicoDietaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6140728129170118104L;

	/**
	 * Busca itens do modelo informado.
	 * 
	 * @param seqModelo
	 */
	public List<ItensModeloBasicoVO> obterListaItensModelo(
			Integer seqModeloBasico) {
		List<ItensModeloBasicoVO> result = new ArrayList<ItensModeloBasicoVO>();

		result.addAll(this.montaDietasModelo(seqModeloBasico));
		result.addAll(this.montaCuidadosModelo(seqModeloBasico));
		result.addAll(this.montaMedicamentoModelo(seqModeloBasico));
		result.addAll(this.montaProcedimentoModelo(seqModeloBasico));

		return result;
	}

	private static final String SEPARADOR_LINHA = "<br/>";

	/**
	 * Testa o objeto recebido para delegar a exclusão para o DAO responsável
	 * pela exclusão do tipo de objeto recebido.
	 * 
	 * @param object
	 */
	public void excluir(Object object) throws ApplicationBusinessException {

		if (object instanceof MpmModeloBasicoDieta) {
			MpmModeloBasicoDieta modeloDietaAux = (MpmModeloBasicoDieta) object;
			MpmModeloBasicoDietaId id = new MpmModeloBasicoDietaId();
			id.setModeloBasicoPrescricaoSeq(modeloDietaAux.getId().getModeloBasicoPrescricaoSeq());
			id.setSeq(modeloDietaAux.getId().getSeq());
			
			MpmModeloBasicoDieta modeloBasicoDieta = this.getMpmModeloBasicoDietaDAO().obterPorChavePrimaria(id);
			this.getMpmModeloBasicoDietaDAO().remover(modeloBasicoDieta);
			this.getMpmModeloBasicoDietaDAO().flush();
			
		} else if (object instanceof MpmModeloBasicoCuidado) {
			this.getManterCuidadosModeloBasicoON().excluir(object);

		} else if (object instanceof MpmModeloBasicoMedicamento) {
			MpmModeloBasicoMedicamento modeloMedicamentoAux = (MpmModeloBasicoMedicamento) object;
			MpmModeloBasicoMedicamentoId id = new MpmModeloBasicoMedicamentoId();
			id.setModeloBasicoPrescricaoSeq(modeloMedicamentoAux.getId().getModeloBasicoPrescricaoSeq());
			id.setSeq(modeloMedicamentoAux.getId().getSeq());
			
			MpmModeloBasicoMedicamento modeloBasicoMedicamento = this.getMpmModeloBasicoMedicamentoDAO().obterPorChavePrimaria(id);
			this.getMpmModeloBasicoMedicamentoDAO().remover(modeloBasicoMedicamento);
			this.getMpmModeloBasicoMedicamentoDAO().flush();
			
		} else if (object instanceof MpmModeloBasicoProcedimento) {
			MpmModeloBasicoProcedimento modeloProcedimentoAux = (MpmModeloBasicoProcedimento) object;
			MpmModeloBasicoProcedimentoId id = new MpmModeloBasicoProcedimentoId();
			id.setModeloBasicoPrescricaoSeq(modeloProcedimentoAux.getId().getModeloBasicoPrescricaoSeq());
			id.setSeq(modeloProcedimentoAux.getId().getSeq());
			
			MpmModeloBasicoProcedimento modeloBasicoProcedimento = this.getMpmModeloBasicoProcedimentoDAO().obterPorChavePrimaria(id);
			this.getMpmModeloBasicoProcedimentoDAO().remover(modeloBasicoProcedimento);
			this.getMpmModeloBasicoProcedimentoDAO().flush();
		}else if ( object instanceof  MpmModeloBasicoModoUsoProcedimento) {

            MpmModeloBasicoModoUsoProcedimento modeloBasicoModoUsoProcedimento = (MpmModeloBasicoModoUsoProcedimento)object;
            MpmModeloBasicoModoUsoProcedimento modeloBasicoProcedimento = this.getMpmModeloBasicoModoUsoProcedimentosDAO().obterPorChavePrimaria(modeloBasicoModoUsoProcedimento.getId());
            this.getMpmModeloBasicoModoUsoProcedimentosDAO().remover(modeloBasicoProcedimento);
            this.getMpmModeloBasicoModoUsoProcedimentosDAO().flush();
        }
        else {
			throw new IllegalArgumentException(
					"Parâmetro fornecido não é do tipo de itens que podem ser excluídos");
		}
	}

	public MpmModeloBasicoPrescricao obterModeloBasico(Integer seqModelo) {
		return this.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(
				seqModelo);
	}
	
	public MpmModeloBasicoPrescricao obterModeloBasicoPrescricaoComServidorPorId(
			Integer seq) throws ApplicationBusinessException {
		return this.getMpmModeloBasicoPrescricaoDAO().obterModeloBasicoPrescricaoComServidorPorId(seq);
	}

	/**
	 * Retorna itens do modelo básico. Retorna descrição concatenando os itens
	 * desta dieta.
	 * 
	 * @param dieta
	 * @return
	 */
	public String getDescricaoEditadaDieta(MpmModeloBasicoDieta dieta) {

		StringBuffer descEditada = new StringBuffer(35);

		// ordenando pela Seq da Dieta

		List<MpmItemModeloBasicoDieta> itens = this
				.getManterDietasModeloBasicoON().obterListaItensDieta(
						dieta.getId().getModeloBasicoPrescricaoSeq(),
						dieta.getId().getSeq());

		Collections.sort(new ArrayList<MpmItemModeloBasicoDieta>(itens),
				new Comparator<MpmItemModeloBasicoDieta>() {
					public int compare(MpmItemModeloBasicoDieta mbd1,
							MpmItemModeloBasicoDieta mbd2) {
						return mbd1
								.getId()
								.getModeloBasicoDietaSeq()
								.compareTo(
										mbd1.getId().getModeloBasicoDietaSeq());
					}
				});

		for (MpmItemModeloBasicoDieta itemDieta : itens) {
			descEditada.append(itemDieta.getDescricaoEditada());
		}

		if ("S".equals(dieta.getIndAvalNutricionista())) {
			descEditada.append(" Avaliação Nutricionista; ");
		}
		
		if(dieta.getIndBombaInfusao()) {
			descEditada.append("BI").append("; ");
		}

		if (dieta.getObservacao() != null) {
			descEditada.append(" " + dieta.getObservacao() + "; ");
		}

		return descEditada.toString();

	}

	/**
	 * Retorna descrição concatenando os itens de medicamentos/solução
	 * 
	 * @param medicamento
	 * @return String
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String getDescricaoEditadaMedicamentoItem(
			MpmModeloBasicoMedicamento medicamento) {
		
		medicamento = getMpmModeloBasicoMedicamentoDAO().obterPorChavePrimaria(medicamento.getId());
		
		StringBuilder strBuilder = new StringBuilder();
		boolean hasFilhos = false;

		// atualiza lista buscando do banco
		List<MpmItemModeloBasicoMedicamento> lista = this
				.getMpmItemModeloBasicoMedicamentoDAO().obterItensMedicamento(
						medicamento.getId().getModeloBasicoPrescricaoSeq(),
						medicamento.getId().getSeq());

		for (MpmItemModeloBasicoMedicamento itensModeloMedicamento : lista) {
			hasFilhos = true;
			strBuilder.append(itensModeloMedicamento.getDescricaoEditada());
			if (medicamento.getIndSolucao()) {
				strBuilder.append(SEPARADOR_LINHA);
				strBuilder.append("    ");

			}
		}
		
		if (hasFilhos) {
			strBuilder.append(' ')
					.append(medicamento.getViaAdministracao().getSigla())
					.append("; ");

			// Por regra do SQL this.getTipoFreqAprazamento() nao deveria ser
			// nulo.
			if (medicamento.getTipoFrequenciaAprazamento() != null) {
				if (StringUtils.isNotBlank(medicamento
						.getTipoFrequenciaAprazamento().getSintaxe())) {
					strBuilder.append(medicamento
							.getTipoFrequenciaAprazamento()
							.getSintaxeFormatada(medicamento.getFrequencia()));
				} else {
					strBuilder.append(medicamento
							.getTipoFrequenciaAprazamento().getDescricao());
				}
				strBuilder.append("; ");
			}

			if (medicamento.getHoraInicioAdministracao() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				strBuilder
						.append("I=")
						.append(sdf.format(medicamento
								.getHoraInicioAdministracao())).append(" h; ");
			}
			
			Locale locBR = new Locale("pt", "BR");//Brasil 
			DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
			dfSymbols.setDecimalSeparator(',');
			DecimalFormat format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
			
			StringBuilder stbDiluente = new StringBuilder();
			if (medicamento.getDiluente() != null) {
				stbDiluente.append(medicamento.getDiluente().getDescricao());
				if (medicamento.getDiluente().getConcentracao() != null) {
					stbDiluente.append(' ').append(
							medicamento.getDiluente().getConcentracaoFormatada());
				}
				if (medicamento.getDiluente().getMpmUnidadeMedidaMedicas() != null
						&& medicamento.getDiluente().getMpmUnidadeMedidaMedicas()
								.getDescricao() != null) {
					stbDiluente.append(' ').append(
							medicamento.getDiluente().getMpmUnidadeMedidaMedicas()
									.getDescricao());
				}
			}

			
			if (stbDiluente.length() > 0) {
				if (medicamento.getVolumeDiluenteMl() != null) {
					strBuilder.append(" Diluir em ").append(
							format.format(medicamento.getVolumeDiluenteMl())).append(" ml de ")
							.append(stbDiluente.toString()).append("; ");
				} else {
					strBuilder.append(" Diluir em ").append(
							stbDiluente.toString()).append("; ");
				}
			} else {
				if (medicamento.getVolumeDiluenteMl() != null) {
					strBuilder.append(" Diluir em ").append(
							format.format(medicamento.getVolumeDiluenteMl())).append(" ml; ");
				}
			}

			if (medicamento.getQuantidadeHorasCorrer() != null) {
				strBuilder.append("Correr em ").append(
						medicamento.getQuantidadeHorasCorrer());
				if (medicamento.getUnidHorasCorrer() == null
						|| DominioUnidadeHorasMinutos.H.equals(medicamento
								.getUnidHorasCorrer())) {
					strBuilder.append(" horas; ");
				} else {
					strBuilder.append(" minutos; ");
				}
			}

			if (medicamento.getGotejo() != null) {
				strBuilder.append("Gotejo ").append(format.format(medicamento.getGotejo()))
						.append(' ');
				if (medicamento.getTipoVelocidadeAdministracao() != null) {
					strBuilder.append(medicamento
							.getTipoVelocidadeAdministracao().getDescricao());
				} else {
					// Tipo velocidade administracao pode ser nulo, no sistema
					// antigo nao era tratado.
					strBuilder
							.append("ERRO: tipo velocidade administracao nao informado");
				}
				strBuilder.append("; ");
			}
			
			if (medicamento.getIndBombaInfusao()) {
				strBuilder.append("BI").append("; ");
			}

			if (medicamento.getIndSeNecessario()) {
				strBuilder.append("Se Necessário; ");
			}

			if (StringUtils.isNotBlank(medicamento.getObservacao())) {
				strBuilder.append(medicamento.getObservacao()).append("; ");
			}
		}
		return strBuilder.toString();
	}
	
	private String getNumeroFormatado(Number value, String fieldName) {
		String numFormated = "";
		if (value != null) {
			numFormated = AghuNumberFormat.formatarValor(value,MpmModeloBasicoMedicamento.class,fieldName);
		}
		return numFormated;
	}
	
	public String getDescricaoEditadaModeloBasicoProcedimento(MpmModeloBasicoProcedimento procedimento) {
		
		if(procedimento.getId() != null){
			procedimento = this.getMpmModeloBasicoProcedimentoDAO().obterPorChavePrimaria(new MpmModeloBasicoProcedimentoId(procedimento.getId().getModeloBasicoPrescricaoSeq(), procedimento.getId().getSeq()), true,
					MpmModeloBasicoProcedimento.Fields.PROCED_ESPECIAL_DIVERSO, MpmModeloBasicoProcedimento.Fields.PROCEDIMENTO_CIRURGICO, MpmModeloBasicoProcedimento.Fields.MATERIAL);
		}
		
		StringBuffer descricao = new StringBuffer();
		if (procedimento.getProcedEspecialDiverso() != null) {
			descricao.append(procedimento.getProcedEspecialDiverso().getDescricao()).append(_PONTO_VIRGULA_);
		}
		if (procedimento.getProcedimentoCirurgico() != null) {
			descricao.append(' ').append(procedimento.getProcedimentoCirurgico().getDescricao()).append(_PONTO_VIRGULA_);
		}
		if (procedimento.getMaterial() != null) {
			descricao.append(' ').append(procedimento.getMaterial().getNome()).append(_PONTO_VIRGULA_);
		}
		if (procedimento.getQuantidade() != null && procedimento.getQuantidade() > 0) {
			descricao.append(' ').append(procedimento.getQuantidade());
		}
		if (procedimento.getMaterial() != null) {
			descricao.append(' ').append(procedimento.getMaterial().getUmdCodigo()).append(_PONTO_VIRGULA_);
		}
		
		return descricao.toString();
		
	}


	public String getDescricaoEditadaProcedimento(
			MpmModeloBasicoProcedimento procedimento) {
		StringBuffer descricao = new StringBuffer();
		String descricaoEditada = this.getDescricaoEditadaModeloBasicoProcedimento(procedimento);
		if (descricaoEditada != null) {

			descricao.append(descricaoEditada);

			List<MpmModeloBasicoModoUsoProcedimento> listMpmModeloBasicoModoUsoProcedimento;

			listMpmModeloBasicoModoUsoProcedimento = this
					.getMpmModeloBasicoModoUsoProcedimentosDAO().pesquisar(
							procedimento);

			for (MpmModeloBasicoModoUsoProcedimento modeloBasicoModoUsoProcedimento : listMpmModeloBasicoModoUsoProcedimento) {
				if (modeloBasicoModoUsoProcedimento.getDescricaoEditada() != null) {
					descricao.append(' ')
							.append(modeloBasicoModoUsoProcedimento
										.getDescricaoEditada()).append(_PONTO_VIRGULA_);
				}
			}
		}

		// CONCATENAR AS INFORMAÇÕES COMPLEMENTARES
		if (StringUtils.isNotBlank(procedimento.getInformacoesComplementares())) {

			descricao.append(" Inf. Complementares=")
					.append(procedimento.getInformacoesComplementares()).append(_PONTO_VIRGULA_);

		}
		return descricao.toString();

	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String getDescricaoEditadaMedicamento(MpmModeloBasicoMedicamento medicamento) {
		StringBuilder strBuilder = new StringBuilder();
		boolean hasFilhos = false;
		
		medicamento = this.getMpmModeloBasicoMedicamentoDAO().obterPorChavePrimaria(medicamento.getId());

		for (MpmItemModeloBasicoMedicamento itensModeloMedicamento : medicamento
				.getItensModeloMedicamento()) {
			hasFilhos = true;
			strBuilder.append(itensModeloMedicamento.getDescricaoEditada());
			if (medicamento.getIndSolucao()) {
				strBuilder.append(" <br/> ");
			}
		}
		// TODO revisar hardcode
		if (hasFilhos) {
			strBuilder.append(' ')
					.append(medicamento.getViaAdministracao().getSigla()).append("; ");

			// Por regra do SQL this.getTipoFreqAprazamento() nao deveria ser
			// nulo.
			if (StringUtils.isNotBlank(medicamento.getTipoFrequenciaAprazamento()
					.getSintaxe())) {
				strBuilder.append(medicamento.getTipoFrequenciaAprazamento()
						.getSintaxeFormatada(medicamento.getFrequencia()));
			} else {
				strBuilder.append(medicamento.getTipoFrequenciaAprazamento()
						.getDescricao());
			}
			strBuilder.append("; ");

			if (medicamento.getHoraInicioAdministracao() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				strBuilder.append("I=")
						.append(sdf.format(medicamento.getHoraInicioAdministracao()))
						.append(" h; ");
			}

			if (medicamento.getQuantidadeHorasCorrer() != null) {
				strBuilder.append("Correr em ").append(
						medicamento.getQuantidadeHorasCorrer());
				if (medicamento.getUnidHorasCorrer() == null
						|| DominioUnidadeHorasMinutos.H.equals(medicamento
								.getUnidHorasCorrer())) {
					strBuilder.append(" horas; ");
				} else {
					strBuilder.append(" minutos; ");
				}
			}

			if (medicamento.getGotejo() != null) {
				strBuilder.append("Velocidade de Infusão ");
				strBuilder.append(getNumeroFormatado(medicamento.getGotejo(), MpmModeloBasicoMedicamento.Fields.GOTEJO.toString()));
				strBuilder.append(' ');
				if (medicamento.getTipoVelocidadeAdministracao() != null) {
					strBuilder.append(medicamento.getTipoVelocidadeAdministracao().getDescricao());
				} else {
					// Tipo velocidade administracao pode ser nulo, no sistema
					// antigo nao era tratado.
					strBuilder.append("ERRO: tipo velocidade administracao nao informado");
				}
				strBuilder.append("; ");
			}
			
			if (medicamento.getIndBombaInfusao()) {
				strBuilder.append("BI").append("; ");
			}

			if (medicamento.getIndSeNecessario()) {
				strBuilder.append("Se Necessário; ");
			}

			if (StringUtils.isNotBlank(medicamento.getObservacao())) {
				strBuilder.append(medicamento.getObservacao()).append("; ");
			}
		}
		return strBuilder.toString();
	}
	
	

	/**
	 * Retorna VOs de itens do modelo básico de Dieta.
	 * 
	 * @param seqModeloBasico
	 * @return
	 */
	private List<ItensModeloBasicoVO> montaDietasModelo(Integer seqModeloBasico) {
		List<ItensModeloBasicoVO> result = new ArrayList<ItensModeloBasicoVO>();

		MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao = this
				.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(
						seqModeloBasico);

		// força a buscar os itens atualizados no bd
		List<MpmModeloBasicoDieta> itens = this.getMpmModeloBasicoDietaDAO()
				.pesquisar(mpmModeloBasicoPrescricao);

		for (MpmModeloBasicoDieta dieta : itens) {
			ItensModeloBasicoVO vo = new ItensModeloBasicoVO();
			vo.setModeloBasicoPrescricaoSeq(seqModeloBasico);

			vo.setItemSeq(dieta.getId().getSeq());
			String descricao = this.getDescricaoEditadaDieta(dieta);
			vo.setDescricao(descricao);

			vo.setTipo(ItensModeloBasicoVO.Tipo.DIETA);
			result.add(vo);
		}

		Collections.sort(result);
		return result;
	}

	/**
	 * Retorna itens do modelo básico de cuidados.
	 * 
	 * @param seqModeloBasico
	 * @return
	 */
	private List<ItensModeloBasicoVO> montaCuidadosModelo(
			Integer seqModeloBasico) {
		List<ItensModeloBasicoVO> result = new ArrayList<ItensModeloBasicoVO>();

		MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao = this
				.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(
						seqModeloBasico);

		// força a buscar os itens atualizados no bd
		List<MpmModeloBasicoCuidado> itens = this
				.getMpmModeloBasicoCuidadoDAO().listar(
						mpmModeloBasicoPrescricao);

		for (MpmModeloBasicoCuidado item : itens) {
			ItensModeloBasicoVO vo = new ItensModeloBasicoVO();
			vo.setModeloBasicoPrescricaoSeq(seqModeloBasico);
			vo.setItemSeq(item.getId().getSeq());
			vo.setDescricao(this.getManterCuidadosModeloBasicoON().obterDescricaoEditadaModeloBasicoCuidado(seqModeloBasico, item.getId().getSeq()));
			vo.setTipo(ItensModeloBasicoVO.Tipo.CUIDADO);
			result.add(vo);
		}

		Collections.sort(result);
		return result;
	}

	/**
	 * Retorna itens do modelo básico de Medicamento.
	 * 
	 * @param seqModeloBasico
	 * @return
	 */
	private List<ItensModeloBasicoVO> montaMedicamentoModelo(
			Integer seqModeloBasico) {
		List<ItensModeloBasicoVO> result = new ArrayList<ItensModeloBasicoVO>();

		MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao = this
				.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(
						seqModeloBasico);

		// força a buscar os itens atualizados no bd
		List<MpmModeloBasicoMedicamento> itens = this
				.getMpmModeloBasicoMedicamentoDAO().pesquisar(
						mpmModeloBasicoPrescricao);

		for (MpmModeloBasicoMedicamento item : itens) {
			ItensModeloBasicoVO vo = new ItensModeloBasicoVO();
			vo.setModeloBasicoPrescricaoSeq(seqModeloBasico);
			vo.setItemSeq(item.getId().getSeq());
			vo.setDescricao(this.getDescricaoEditadaMedicamentoItem(item));

			if (item.getIndSolucao()) {
				vo.setTipo(ItensModeloBasicoVO.Tipo.SOLUCAO);
			} else {
				vo.setTipo(ItensModeloBasicoVO.Tipo.MEDICAMENTO);
			}
			result.add(vo);
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * Retorna itens do modelo básico de Procedimentos.
	 * 
	 * @param seqModeloBasico
	 * @return
	 */
	private List<ItensModeloBasicoVO> montaProcedimentoModelo(
			Integer seqModeloBasico) {
		List<ItensModeloBasicoVO> result = new ArrayList<ItensModeloBasicoVO>();

		MpmModeloBasicoPrescricao mpmModeloBasicoPrescricao = this
				.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(
						seqModeloBasico);

		// força a buscar os itens atualizados no bd
		List<MpmModeloBasicoProcedimento> itens = this
				.getMpmModeloBasicoProcedimentoDAO().pesquisar(
						mpmModeloBasicoPrescricao);

		for (MpmModeloBasicoProcedimento item : itens) {
			ItensModeloBasicoVO vo = new ItensModeloBasicoVO();
			vo.setModeloBasicoPrescricaoSeq(seqModeloBasico);
			vo.setItemSeq(item.getId().getSeq().intValue());
			vo.setDescricao(this.getDescricaoEditadaProcedimento(item));
			vo.setTipo(ItensModeloBasicoVO.Tipo.PROCEDIMENTO);
			result.add(vo);
		}
		Collections.sort(result);
		return result;
	}

	// getters & setters

	protected ManterDietasModeloBasicoON getManterDietasModeloBasicoON() {
		return manterDietasModeloBasicoON;
	}

	protected ManterCuidadosModeloBasicoON getManterCuidadosModeloBasicoON() {
		return manterCuidadosModeloBasicoON;
	}

	protected MpmModeloBasicoDietaDAO getMpmModeloBasicoDietaDAO() {
		return mpmModeloBasicoDietaDAO;
	}

	protected MpmModeloBasicoPrescricaoDAO getMpmModeloBasicoPrescricaoDAO() {
		return mpmModeloBasicoPrescricaoDAO;
	}

	protected MpmModeloBasicoCuidadoDAO getMpmModeloBasicoCuidadoDAO() {
		return mpmModeloBasicoCuidadoDAO;
	}

	protected MpmModeloBasicoMedicamentoDAO getMpmModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}

	protected MpmModeloBasicoProcedimentoDAO getMpmModeloBasicoProcedimentoDAO() {
		return mpmModeloBasicoProcedimentoDAO;
	}

	protected MpmModeloBasicoModoUsoProcedimentoDAO getMpmModeloBasicoModoUsoProcedimentosDAO() {
		return mpmModeloBasicoModoUsoProcedimentoDAO;
	}

	protected MpmItemModeloBasicoMedicamentoDAO getMpmItemModeloBasicoMedicamentoDAO() {
		return mpmItemModeloBasicoMedicamentoDAO;
	}
}
