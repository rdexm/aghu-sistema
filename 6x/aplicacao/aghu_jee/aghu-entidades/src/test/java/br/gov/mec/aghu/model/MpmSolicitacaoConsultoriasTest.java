/**
 * 
 */
package br.gov.mec.aghu.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;

/**
 * @author rcorvalao
 *
 */
public class MpmSolicitacaoConsultoriasTest {
	
	private MpmSolicitacaoConsultoria umSolicitacaoConsultoria;
	
	@Before
    public void doBeforeEachTestCase() {
		umSolicitacaoConsultoria = new MpmSolicitacaoConsultoria();
    }
	
	/**
	 * Verifica se pega as informações apenas com o objeto instanciado, deve dar erro null pointer.
	 */
	@Test(expected=NullPointerException.class)
	public void getDescricaoFormatada001Test() {
		umSolicitacaoConsultoria.getDescricaoFormatada();		
	}
	
	/**
	 * Verifica formatacao de string para:
	 * <br>
	 * urgencia: nao urgente,
	 * tipo: Avaliacao Pre Cirurgica,
	 * nomeEspecialidade: Especialidade 1.
	 * 
	 */
	@Test()
	public void getDescricaoFormatada002Test() {
		String nomeEspecialidade = "Especialidade 1";
		
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade(nomeEspecialidade);
		umSolicitacaoConsultoria.setEspecialidade(especialidade);
		umSolicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.A);
		umSolicitacaoConsultoria.setUrgente(false);
		
		String atual = umSolicitacaoConsultoria.getDescricaoFormatada();
		
		String str = MpmSolicitacaoConsultoria.FIELD_TIPO_OUTROS + " " + MpmSolicitacaoConsultoria.FIELD_INDICADOR_URGENTE_NAO + " " + nomeEspecialidade;

		assertEquals(str, atual);
	}
	
	/**
	 * Verifica formatacao de string para:
	 * <br>
	 * urgencia: urgente,
	 * tipo: Avaliacao Pre Cirurgica,
	 * nomeEspecialidade: Especialidade 1.
	 * 
	 */
	@Test()
	public void getDescricaoFormatada003Test() {
		String nomeEspecialidade = "Especialidade 1";
		
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade(nomeEspecialidade);
		umSolicitacaoConsultoria.setEspecialidade(especialidade);
		umSolicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.A);
		umSolicitacaoConsultoria.setUrgente(true);
		
		String atual = umSolicitacaoConsultoria.getDescricaoFormatada();
		
		String str = MpmSolicitacaoConsultoria.FIELD_TIPO_OUTROS + " " + MpmSolicitacaoConsultoria.FIELD_INDICADOR_URGENTE_SIM + " " + nomeEspecialidade;

		assertEquals(str, atual);
	}
	
	/**
	 * Verifica formatacao de string para:
	 * <br>
	 * urgencia: nao urgente,
	 * tipo: Consultoria,
	 * nomeEspecialidade: Especialidade 1.
	 * 
	 */
	@Test()
	public void getDescricaoFormatada004Test() {
		String nomeEspecialidade = "Especialidade 1";
		
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade(nomeEspecialidade);
		umSolicitacaoConsultoria.setEspecialidade(especialidade);
		umSolicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.C);
		umSolicitacaoConsultoria.setUrgente(false);
		
		String atual = umSolicitacaoConsultoria.getDescricaoFormatada();
		
		String str = MpmSolicitacaoConsultoria.FIELD_TIPO_CONSULTORIA + " " + MpmSolicitacaoConsultoria.FIELD_INDICADOR_URGENTE_NAO + " " + nomeEspecialidade;

		assertEquals(str, atual);
	}
	
	/**
	 * Verifica formatacao de string para:
	 * <br>
	 * urgencia: urgente,
	 * tipo: Consultoria,
	 * nomeEspecialidade: Especialidade 1.
	 * 
	 */
	@Test()
	public void getDescricaoFormatada005Test() {
		String nomeEspecialidade = "Especialidade 1";
		
		AghEspecialidades especialidade = new AghEspecialidades();
		especialidade.setNomeEspecialidade(nomeEspecialidade);
		umSolicitacaoConsultoria.setEspecialidade(especialidade);
		umSolicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.C);
		umSolicitacaoConsultoria.setUrgente(true);
		
		String atual = umSolicitacaoConsultoria.getDescricaoFormatada();
		
		String str = MpmSolicitacaoConsultoria.FIELD_TIPO_CONSULTORIA + " " + MpmSolicitacaoConsultoria.FIELD_INDICADOR_URGENTE_SIM + " " + nomeEspecialidade;

		assertEquals(str, atual);
	}
}
