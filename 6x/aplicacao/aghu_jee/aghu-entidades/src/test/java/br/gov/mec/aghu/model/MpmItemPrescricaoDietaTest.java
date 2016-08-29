package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author bsoliveira
 *
 */
public class MpmItemPrescricaoDietaTest {
	
	private MpmItemPrescricaoDieta systemUnderTest;
	
	@Before
    public void doBeforeEachTestCase() {
    	
        systemUnderTest = new MpmItemPrescricaoDieta();
        
    }
	
	/**
	 * Verifica se pega a informação de descrição de dieta.
	 */
	@Test
	public void getDescricaoFormatada001Test() {
		
		AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta();
		tipoItemDieta.setDescricao("TESTE");
		systemUnderTest.setTipoItemDieta(tipoItemDieta);
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals("TESTE; ", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de descrição de dieta e quantidade.
	 */
	@Test
	public void getDescricaoFormatada002Test() {
		
		AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta();
		tipoItemDieta.setDescricao("TESTE");
		systemUnderTest.setTipoItemDieta(tipoItemDieta);
		
		systemUnderTest.setQuantidade(BigDecimal.valueOf(1001));
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals("TESTE 1.001; ", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de descrição de dieta e 
	 * unidade medida medica e quantidade.
	 */
	@Test
	public void getDescricaoFormatada003Test() {
		
		AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta();
		MpmUnidadeMedidaMedica unidadeMedidaMedica = new MpmUnidadeMedidaMedica();
		
		unidadeMedidaMedica.setDescricao("TESTE2");
		
		tipoItemDieta.setUnidadeMedidaMedica(unidadeMedidaMedica);
		tipoItemDieta.setDescricao("TESTE1");

		systemUnderTest.setTipoItemDieta(tipoItemDieta);
		
		systemUnderTest.setQuantidade(BigDecimal.valueOf(1001));
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals("TESTE1 1.001 TESTE2; ", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de sintaxe de frequencia
	 * de aprazamento e da frequencia analisando se está fazendo o
	 * replace corretamente.
	 */
	@Test
	public void getDescricaoFormatada004Test() {
		
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setSintaxe("T#ES#TE1#");
		
		systemUnderTest.setTipoFreqAprazamento(tipoFrequenciaAprazamento);
		
		systemUnderTest.setFrequencia(Short.valueOf("10"));
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals(" T10ES10TE110; ", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de descrição de frequencia
	 * de aprazamento.
	 */
	@Test
	public void getDescricaoFormatada005Test() {
		
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setDescricao("TESTE1");
		
		systemUnderTest.setTipoFreqAprazamento(tipoFrequenciaAprazamento);
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals(" TESTE1; ", atual);
		
	}
	
	/**
	 * Verifica se pega a informação de numero de vezes.
	 */
	@Test
	public void getDescricaoFormatada006Test() {
		
		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setDescricao("TESTE1");
		
		systemUnderTest.setTipoFreqAprazamento(tipoFrequenciaAprazamento);
		systemUnderTest.setNumVezes(Byte.valueOf("2"));
		
		String atual = systemUnderTest.getDescricaoFormatada();
		
		assertEquals(" TESTE1, número de vezes: 2; ", atual);
		
	}

}
