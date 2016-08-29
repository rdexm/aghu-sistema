/**
 * 
 */
package br.gov.mec.aghu.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.core.utils.AghuNumberFormat;

/**
 * @author rcorvalao
 *
 */
public class MpmItemPrescricaoMdtosTest {
	
	private static final String ITEM_MEDICAMENTO_DESCRICAO = "Medicamento 1";
	private static final String ITEM_MEDICAMENTO_TPR_SIGLA = "TPR_sigla";
	private static final BigDecimal ITEM_DOSE = new BigDecimal("1512.5287");
	private static final String ITEM_DOSE_FORMATADA = AghuNumberFormat.formatarValor(new BigDecimal("1512.5287"), "#######,###.####");
	private static final BigDecimal ITEM_MEDICAMENTO_CONCENTRACAO = new BigDecimal("15.6549");
	private static final String ITEM_MEDICAMENTO_CONCENTRACAO_FORMATADA = AghuNumberFormat.formatarValor(new BigDecimal("15.6549"), "##########.####");
	private static final String ITEM_OBSERVACAO = "observacao descritiva 1";
	private static final String ITEM_MEDICAMENTO_UNIDADEMEDIDAMEDICA_DESCRICAO = "Unid. Medica Medica 1";;
	
	
	private MpmItemPrescricaoMdto umMpmItemPrescricaoMdtos;

	@Before
	public void doBeforeEachTestCase() {
		umMpmItemPrescricaoMdtos = new MpmItemPrescricaoMdto();
	}
	
	private AfaMedicamento getAfaMedicamento() {
		AfaMedicamento medicamento = new AfaMedicamento();
		
		medicamento.setDescricao(ITEM_MEDICAMENTO_DESCRICAO);
		medicamento.setTipoApresentacaoMedicamento(new AfaTipoApresentacaoMedicamento(ITEM_MEDICAMENTO_TPR_SIGLA, null, null));
		
		return medicamento;
	}
	
	/**
	 * rcorvalao
	 * 07/10/2010
	 */
	private void loadItemPrescricaoMedicamentoFluxoBasico() {
		umMpmItemPrescricaoMdtos.setDose(ITEM_DOSE);
		umMpmItemPrescricaoMdtos.setMedicamento(this.getAfaMedicamento());
		
		MpmPrescricaoMdto mdto = new MpmPrescricaoMdto();
		mdto.setIndSolucao(Boolean.FALSE);
		umMpmItemPrescricaoMdtos.setPrescricaoMedicamento(mdto);
	}

	/**
	 * Verifica se pega as informações apenas com o objeto instanciado, deve retornar null poiter.
	 */
	@Test(expected=NullPointerException.class)
	public void getDescricaoFormatada001Test() {
		String atual = umMpmItemPrescricaoMdtos.getDescricaoFormatada();
		
		assertEquals("", atual);
	}
	
	/**
	 * Verifica fluxo basico do metodo.
	 */
	@Test
	public void getDescricaoFormatada002Test() {
		this.loadItemPrescricaoMedicamentoFluxoBasico();
		
		String atual = umMpmItemPrescricaoMdtos.getDescricaoFormatada();
		
		String str = ITEM_MEDICAMENTO_DESCRICAO + MpmItemPrescricaoMdto.ADMINISTRAR_DOSE + ITEM_DOSE_FORMATADA + " " + ITEM_MEDICAMENTO_TPR_SIGLA + ";";
		
		assertEquals(str, atual);
	}

	/**
	 * Verifica fluxo basico do metodo. Mais concentracao.
	 */
	@Test
	public void getDescricaoFormatada003Test() {
		this.loadItemPrescricaoMedicamentoFluxoBasico();
		
		umMpmItemPrescricaoMdtos.getMedicamento().setConcentracao(ITEM_MEDICAMENTO_CONCENTRACAO);
		
		String atual = umMpmItemPrescricaoMdtos.getDescricaoFormatada();
		
		String str = ITEM_MEDICAMENTO_DESCRICAO + " " + ITEM_MEDICAMENTO_CONCENTRACAO_FORMATADA + MpmItemPrescricaoMdto.ADMINISTRAR_DOSE + ITEM_DOSE_FORMATADA + " " + ITEM_MEDICAMENTO_TPR_SIGLA + ";";
		
		assertEquals(str, atual);
	}
	
	/**
	 * Verifica fluxo basico do metodo. Mais observacao.
	 */
	@Test
	public void getDescricaoFormatada004Test() {
		this.loadItemPrescricaoMedicamentoFluxoBasico();
		
		umMpmItemPrescricaoMdtos.setObservacao(ITEM_OBSERVACAO);
		
		String atual = umMpmItemPrescricaoMdtos.getDescricaoFormatada();
		
		String str = ITEM_MEDICAMENTO_DESCRICAO + " : " + ITEM_OBSERVACAO + MpmItemPrescricaoMdto.ADMINISTRAR_DOSE + ITEM_DOSE_FORMATADA + " " + ITEM_MEDICAMENTO_TPR_SIGLA + ";";
		
		assertEquals(str, atual);
	}
	
	/**
	 * Verifica fluxo basico do metodo. Mais unidadeMedidaMedica.
	 */
	@Test
	public void getDescricaoFormatada005Test() {
		this.loadItemPrescricaoMedicamentoFluxoBasico();
		
		MpmUnidadeMedidaMedica unidadeMedidaMedica = new MpmUnidadeMedidaMedica();
		unidadeMedidaMedica.setDescricao(ITEM_MEDICAMENTO_UNIDADEMEDIDAMEDICA_DESCRICAO);
		umMpmItemPrescricaoMdtos.getMedicamento().setMpmUnidadeMedidaMedicas(unidadeMedidaMedica);
		
		String atual = umMpmItemPrescricaoMdtos.getDescricaoFormatada();
		
		String str = ITEM_MEDICAMENTO_DESCRICAO + " " + ITEM_MEDICAMENTO_UNIDADEMEDIDAMEDICA_DESCRICAO + MpmItemPrescricaoMdto.ADMINISTRAR_DOSE + ITEM_DOSE_FORMATADA + " " + ITEM_MEDICAMENTO_TPR_SIGLA + ";";
		
		assertEquals(str, atual);
	}
	
	/**
	 * Verifica fluxo basico do metodo. Mais formaDosagem.unidadeMedidaMedica.descricao.
	 */
	@Test
	public void getDescricaoFormatada006Test() {
		this.loadItemPrescricaoMedicamentoFluxoBasico();
		
		MpmUnidadeMedidaMedica unidadeMedidaMedica = new MpmUnidadeMedidaMedica();
		unidadeMedidaMedica.setDescricao(ITEM_MEDICAMENTO_UNIDADEMEDIDAMEDICA_DESCRICAO);
		AfaFormaDosagem formaDosagem = new AfaFormaDosagem();
		formaDosagem.setUnidadeMedidaMedicas(unidadeMedidaMedica);
		umMpmItemPrescricaoMdtos.setFormaDosagem(formaDosagem);
		
		String atual = umMpmItemPrescricaoMdtos.getDescricaoFormatada();
		
		String str = ITEM_MEDICAMENTO_DESCRICAO + MpmItemPrescricaoMdto.ADMINISTRAR_DOSE + ITEM_DOSE_FORMATADA + " " + ITEM_MEDICAMENTO_UNIDADEMEDIDAMEDICA_DESCRICAO + ";";
		
		assertEquals(str, atual);
	}

}
